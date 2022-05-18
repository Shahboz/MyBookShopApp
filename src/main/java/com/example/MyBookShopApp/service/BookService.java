package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookRepository;
import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.google.api.books.Item;
import com.example.MyBookShopApp.entity.google.api.books.Root;
import com.example.MyBookShopApp.exceptions.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public BookService(BookRepository bookRepository, RestTemplate restTemplate, BookstoreUserRegister userRegister) {
        this.bookRepository = bookRepository;
        this.restTemplate = restTemplate;
        this.userRegister = userRegister;
    }

    public Page<Book> getPageOfNewBooks(Integer offset, Integer limit, Date fromDate, Date toDate) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        if(fromDate == null && toDate == null)
            return bookRepository.findByOrderByPubDateDesc(nextPage);
        else if(fromDate == null && toDate != null)
            return bookRepository.findByPubDateLessThanEqualOrderByPubDateDesc(toDate, nextPage);
        else if(fromDate != null && toDate == null)
            return bookRepository.findByPubDateGreaterThanEqualOrderByPubDateDesc(fromDate, nextPage);
        return bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(fromDate, toDate, nextPage);
    }

    public Page<Book> getPageOfRecommendedBooks(Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        User currentUser = (User) userRegister.getCurrentUser();
        Integer countUserBooks = (currentUser == null ? 0 : bookRepository.getCountUserRecommendedBooks(currentUser.getId()));
        return currentUser == null || countUserBooks == 0 ? bookRepository.findRecommendBooksByRate(nextPage) : bookRepository.findRecommendBooksByUser(currentUser.getId(), nextPage);
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
        return bookRepository.findBooksByAuthorNameContaining(authorSlug, nextPage);
    }

    public Page<Book> getPageOfGenreBooks(String genreSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return bookRepository.findBooksByGenreSlug(genreSlug, nextPage);
    }

    public Page<Book> getPageOfTagBooks(String tagSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return bookRepository.findBooksByTagSlug(tagSlug, nextPage);
    }

    // New BookService methods for RESP api documentation
    public List<Book> getBooksByTitle(String title) throws BookstoreApiWrongParameterException {
        if (title.equals("") || title.length() <= 1)
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        else {
            List<Book> bookList = bookRepository.findBooksByTitleContaining(title);
            if (bookList.size() > 0)
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
        return bookRepository.findBookBySlugEquals(bookSlug);
    }

    public Book getBookById(Integer bookId) {
        return bookRepository.findBookById(bookId);
    }

    public void save(Book bookToUpdate) {
        bookRepository.save(bookToUpdate);
    }

    public List<Book> getBooksBySlugs(String[] slugs) {
        return bookRepository.findBooksBySlugIn(slugs);
    }

    @Value("${google.books.api.key}")
    private String googleApiKey;

    public List<Book> getPageOfGoogleBooksApiSearchResult(String searchWord, Integer offset, Integer limit) {
        String requestUrl = "https://www.googleapis.com/books/v1/volumes" +
                "?q=" + searchWord +
                "&key=" + googleApiKey +
                "&filter=paid-ebooks" +
                "&startIndex=" + offset +
                "&maxResult=" + limit;
        Root root = restTemplate.getForEntity(requestUrl, Root.class).getBody();
        ArrayList<Book> bookList = new ArrayList<>();
        if (root != null) {
            for (Item item : root.getItems()) {
                Book book = new Book();
                if (item.getVolumeInfo() != null) {
                    List<Author> authorList = new ArrayList<>();
                    authorList.add(new Author(item.getVolumeInfo().getAuthors()));
                    book.setAuthors(authorList);
                    book.setTitle(item.getVolumeInfo().getTitle());
                    book.setImage(item.getVolumeInfo().getImageLinks().getThumbnail());
                }
                if (item.getSaleInfo() != null) {
                    Double price = item.getSaleInfo().getRetailPrice().getAmount();
                    book.setPrice(price.intValue());
                    Double oldPrice = item.getSaleInfo().getListPrice().getAmount();
                    book.setDiscount((int) (100 * (oldPrice - price)/ oldPrice));

                }
                bookList.add(book);
            }
        }
        return bookList;
    }

}