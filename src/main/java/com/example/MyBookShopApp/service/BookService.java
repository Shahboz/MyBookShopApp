package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.exceptions.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;


@Service
public class BookService {

    @Value("${book.refresh.offset}")
    private Integer refreshOffset;

    @Value("${book.refresh.limit}")
    private Integer refreshLimit;

    @Value("${book.search.limit}")
    private Integer searchLimit;

    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final BookFileService bookFileService;
    private final FileDownloadService fileDownloadService;

    @Autowired
    public BookService(BookRepository bookRepository, BookstoreUserRegister userRegister,
                       BookFileService bookFileService, FileDownloadService fileDownloadService) {
        this.bookRepository = bookRepository;
        this.userRegister = userRegister;
        this.bookFileService = bookFileService;
        this.fileDownloadService = fileDownloadService;
    }

    public Integer getRefreshOffset() {
        return refreshOffset;
    }

    public Integer getRefreshLimit() {
        return refreshLimit;
    }

    public Integer getSearchLimit() {
        return searchLimit;
    }

    public Page<Book> getPageOfNewBooks(Integer offset, Integer limit, Date fromDate, Date toDate) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        if (fromDate == null) {
            if (toDate == null)
                return bookRepository.findByOrderByPubDateDesc(nextPage);
            else
                return bookRepository.findByPubDateLessThanEqualOrderByPubDateDesc(toDate, nextPage);
        } else if (toDate == null)
            return bookRepository.findByPubDateGreaterThanEqualOrderByPubDateDesc(fromDate, nextPage);
        return bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(fromDate, toDate, nextPage);
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        User currentUser = (User) userRegister.getCurrentUser();
        int countUserBooks = (currentUser == null ? 0 : bookRepository.getCountUserRecommendedBooks(currentUser.getId()));
        return currentUser == null || countUserBooks == 0 ?
                bookRepository.findRecommendBooksByRate(nextPage) : bookRepository.findRecommendBooksByUser(currentUser.getId(), nextPage);
    }

    public Page<Book> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        if(searchWord == null)
            return bookRepository.findAll(nextPage);
        return bookRepository.findBookByTitleContaining(searchWord, nextPage);
    }

    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return bookRepository.findBooksByPopular(nextPage);
    }

    public Page<Book> getPageOfAuthorBooks(String authorSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return StringUtils.isEmpty(authorSlug) ? Page.empty() : bookRepository.findBooksByAuthorNameContaining(authorSlug, nextPage);
    }

    public Page<Book> getPageOfGenreBooks(String genreSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return StringUtils.isEmpty(genreSlug) ? Page.empty() : bookRepository.findBooksByGenreSlug(genreSlug, nextPage);
    }

    public Page<Book> getPageOfTagBooks(String tagSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return StringUtils.isEmpty(tagSlug) ? Page.empty() : bookRepository.findBooksByTagSlug(tagSlug, nextPage);
    }

    // New BookService methods for RESP api documentation
    public List<Book> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        if (StringUtils.isEmpty(title) || title.length() <= 1)
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        else {
            List<Book> bookList = bookRepository.findBooksByTitleContaining(title);
            if (!bookList.isEmpty())
                return bookList;
            else
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
        }
    }

    public List<Book> getBooksWithPriceBetween(Integer priceMin, Integer priceMax) {
        return bookRepository.findBooksByPriceBetween(priceMin, priceMax);
    }

    public List<Book> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    public List<Book> getBooksWithMaxDiscount() {
        return bookRepository.getBooksWithMaxDiscount();
    }

    public Book getBookBySlug(String bookSlug) {
        return StringUtils.isEmpty(bookSlug) ? null : bookRepository.findBookBySlugEquals(bookSlug);
    }

    public Book getBookById(Integer bookId) {
        return bookRepository.findBookById(bookId);
    }

    public Integer getCountBooks() {
        return Math.toIntExact(bookRepository.count());
    }

    public Integer getBookCountByTitle(String bookSlug) {
        return bookRepository.countByTitleContaining(bookSlug);
    }

    public List<Book> getBooksBySlugs(String[] slugs) {
        return bookRepository.findBooksBySlugIn(slugs);
    }

    public void saveBookImage(MultipartFile file, String bookSlug) throws IOException {
        String savePath = bookFileService.saveBookImage(file, bookSlug);
        Book bookToUpdate = bookRepository.findBookBySlugEquals(bookSlug);
        if (bookToUpdate != null) {
            bookToUpdate.setImage(savePath);
            bookRepository.save(bookToUpdate);
        }
    }

    public Boolean limitDownloadExceded(String bookSlug) {
        return !StringUtils.isEmpty(bookSlug) && fileDownloadService.isLimitDownloadExceeded(bookRepository.findBookBySlugEquals(bookSlug));
    }

    public ResponseEntity<ByteArrayResource> downloadBookFile(String fileHash) throws IOException {
        Book bookByFilHash = bookFileService.getBookByFileHash(fileHash);
        if (bookByFilHash != null && !fileDownloadService.isLimitDownloadExceeded(bookByFilHash) && fileDownloadService.saveDownloadBook(bookByFilHash)) {
            byte[] data = bookFileService.getBookFileByteArray(fileHash);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + bookFileService.getBookFilePath(fileHash).getFileName().toString())
                    .contentType(bookFileService.getBookFileMime(fileHash))
                    .contentLength(data.length)
                    .body(new ByteArrayResource(data));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public List<BookFileDto> getBookFilesData(String bookSlug) throws IOException {
        List<BookFileDto> bookFileDtoList = new ArrayList<>();
        List<BookFile> bookFiles = StringUtils.isEmpty(bookSlug) ? Collections.emptyList() : bookFileService.getBookFilesByBookSlug(bookSlug);
        if (bookFiles != null && !bookFiles.isEmpty()) {
            for (BookFile bookFile : bookFiles) {
                double fileSize = Math.ceil(bookFileService.getBookFileByteArray(bookFile.getHash()).length / 1024.0);
                BookFileDto bookFileDto = new BookFileDto(bookFile, (int) fileSize);
                bookFileDtoList.add(bookFileDto);
            }
        }
        return bookFileDtoList;
    }

    public BookInfoDto getBookInfoDto(String bookSlug) throws IOException {
        BookInfoDto bookInfoDto = null;
        Book book = bookRepository.findBookBySlugEquals(bookSlug);
        if (book != null) {
            bookInfoDto = new BookInfoDto(book);
            bookInfoDto.setGenreList(book.getGenresList());
            bookInfoDto.setTagList(book.getTagsList());
            bookInfoDto.setBookFileDtoList(getBookFilesData(bookSlug));
        }
        return bookInfoDto;
    }

    public void saveBook(BookInfoDto bookInfoDto) {
        if (bookInfoDto != null) {
            boolean isChange = false;
            Book book = bookRepository.findBookBySlugEquals(bookInfoDto.getSlug());
            if (book == null) {
                Book newBook = new Book();
                newBook.setTitle(bookInfoDto.getTitle());
                newBook.setSlug(bookInfoDto.getSlug());
                newBook.setPubDate(DateFormatter.parseDate(bookInfoDto.getPubDate()));
                newBook.setPrice(bookInfoDto.getPrice());
                newBook.setDiscount(bookInfoDto.getDiscount());
                newBook.setImage(bookInfoDto.getImage());
                newBook.setDescription(bookInfoDto.getDescription());
                newBook.setIsBestseller(bookInfoDto.getIsBestseller() ? 1 : 0);
                bookRepository.save(newBook);
            } else {
                if (!StringUtils.isEmpty(bookInfoDto.getTitle()) && !book.getTitle().equals(bookInfoDto.getTitle())) {
                    isChange = true;
                    book.setTitle(bookInfoDto.getTitle());
                }
                if (!StringUtils.isEmpty(bookInfoDto.getPubDate())) {
                    Date newPubDate = DateFormatter.parseDate(bookInfoDto.getPubDate());
                    if (!book.getPubDate().equals(newPubDate)) {
                        isChange = true;
                        book.setPubDate(newPubDate);
                    }
                }
                if (!StringUtils.isEmpty(bookInfoDto.getDescription()) && !book.getDescription().equals(bookInfoDto.getDescription())) {
                    isChange = true;
                    book.setDescription(bookInfoDto.getDescription());
                }
                if (bookInfoDto.getPrice() > 0 && !book.getPrice().equals(bookInfoDto.getPrice())) {
                    isChange = true;
                    book.setPrice(bookInfoDto.getPrice());
                }
                if (bookInfoDto.getDiscount() >= 0 && !book.getDiscount().equals(bookInfoDto.getDiscount())) {
                    isChange = true;
                    book.setDiscount(bookInfoDto.getDiscount());
                }
                if (!book.getIsBestseller().equals(bookInfoDto.getIsBestseller() ? 1 : 0)) {
                    isChange = true;
                    book.setIsBestseller(bookInfoDto.getIsBestseller() ? 1 : 0);
                }
                if (isChange) {
                    bookRepository.save(book);
                }
            }
        }
    }

    public void deleteBook(String bookSlug) {
        Book book = bookRepository.findBookBySlugEquals(bookSlug);
        if (book != null) {
            bookRepository.delete(book);
        }
    }

}