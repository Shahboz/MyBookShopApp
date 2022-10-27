package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.AuthorBookDto;
import com.example.MyBookShopApp.dto.AuthorDto;
import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.AuthorBooks;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookReview;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AdminBookService {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookReviewService bookReviewService;
    private final UserBooksService userBooksService;

    @Autowired
    public AdminBookService(BookService bookService, AuthorService authorService, BookReviewService bookReviewService,
                            UserBooksService userBooksService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookReviewService = bookReviewService;
        this.userBooksService = userBooksService;
    }

    public BookInfoDto getBookInfoDto(String bookSlug) throws IOException {
        BookInfoDto bookInfoDto = StringUtils.isEmpty(bookSlug) ? new BookInfoDto() : bookService.getBookInfoDto(bookSlug);
        if (!StringUtils.isEmpty(bookSlug) && bookInfoDto != null) {
            bookInfoDto.setAuthorBooksList(authorService.getAuthorsBook(bookSlug));
            bookInfoDto.setBookReviewDtoList(bookReviewService.getBookReviewData(bookSlug));
        }
        return bookInfoDto;
    }

    public List<AuthorDto> getAuthors(String bookSlug) {
        return StringUtils.isEmpty(bookSlug) ? new ArrayList<>() :
                authorService.getAllAuthors().stream().map(author -> new AuthorDto(author.getName(), author.getSlug())).collect(Collectors.toList());
    }

    public Integer getCountPage() {
        return (int) Math.ceil(bookService.getCountBooks() / (bookService.getRefreshOffset() * 1.0));
    }

    public List<BookInfoDto> getPageOfBooks(Integer page) {
        return bookService.getPageOfNewBooks((page - 1) * bookService.getRefreshLimit(), bookService.getRefreshLimit(), null, null)
                .getContent()
                .stream()
                .map(book -> new BookInfoDto(book))
                .collect(Collectors.toList());
    }

    public void saveBook(BookInfoDto bookInfoDto) throws ParseException {
        bookService.saveBook(bookInfoDto);
    }

    public ResultResponse deleteBook(String bookSlug) {
        ResultResponse resultResponse = new ResultResponse();
        if (!StringUtils.isEmpty(bookSlug)) {
            Book book = bookService.getBookBySlug(bookSlug);
            if (book != null) {
                Integer countSoldBooks = userBooksService.getCountBooks(bookSlug, "PAID");
                Integer countArchivedBooks = userBooksService.getCountBooks(bookSlug, "ARCHIVED");
                if (countSoldBooks == 0 && countArchivedBooks == 0) {
                    bookService.deleteBook(book);
                    resultResponse.setResult(true);
                } else {
                    resultResponse.setResult(false);
                    resultResponse.setError("Невозможно удалить проданную книгу");
                }
            } else {
                resultResponse.setResult(false);
                resultResponse.setError("Книга не найдена");
            }
        } else {
            resultResponse.setResult(false);
            resultResponse.setError("Не задан идентификатор книги для удаления");
        }
        return resultResponse;
    }

    public void saveBookImage(MultipartFile file, String bookSlug) throws IOException {
        bookService.saveBookImage(file, bookSlug);
    }

    public void addAuthorBook(AuthorBookDto authorBookDto) {
        Author author = authorService.getAuthorBySlug(authorBookDto.getAuthorSlug());
        Book book = bookService.getBookBySlug(authorBookDto.getBookSlug());
        if (author != null && book != null && !book.getAuthors().contains(author)) {
            List<AuthorBooks> authorBooksList = authorService.getAuthorsBook(book.getSlug());
            Integer maxSortIndex = authorBooksList.size() > 0 ? authorBooksList.stream().max(Comparator.comparing(AuthorBooks::getSortIndex)).get().getSortIndex() : 0;
            AuthorBooks authorBooks = new AuthorBooks(book, author, maxSortIndex + 1);
            authorService.saveAuthorBook(authorBooks);
        }
    }

    public void deleteAuthorBook(Integer authorBookId) {
        AuthorBooks authorBook = authorService.getAuthorBooks(authorBookId);
        if (authorBook != null) {
            authorService.deleteAuthorBooks(authorBook);
        }
    }

    public void deleteReview(Integer reviewId) {
        BookReview bookReview = bookReviewService.getBookReviewById(reviewId);
        if (bookReview != null) {
            bookReviewService.deleteBookReview(bookReview);
        }
    }

}