package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookRepository;
import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.Genre;
import com.example.MyBookShopApp.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
        return bookRepository.findAll(nextPage);
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
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findBooksByTitleContaining(title);
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

    public Author getAuthorBySlug(String authorSlug) {
        return bookRepository.findAuthor(authorSlug);
    }

    public Genre getGenreBySlug(String genreSlug) {
        return bookRepository.findGenre(genreSlug);
    }

    public Map<Tag, Integer> getTags() {
        Map<Tag, Integer> tagBooks= new HashMap<>();
        List<Tag> tagList = bookRepository.getTags();
        tagList.forEach(tag -> {
            tagBooks.putIfAbsent(tag, 0);
            tagBooks.compute(tag, (key, value) -> value + tag.getBooks().size());
        });
        return tagBooks;
    }

}
