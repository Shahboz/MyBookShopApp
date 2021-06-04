package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookRepository;
import com.example.MyBookShopApp.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooksData() {
        return bookRepository.findAll();
    }

    public List<Book> getPopularBooks() {
        return bookRepository.findByOrderByIsBestsellerDesc();
    }

    public List<Book> getNewBooks(Date fromDate, Date toDate) {
        if(fromDate == null && toDate == null)
            return bookRepository.findByOrderByPubDateDesc();
        else if(fromDate == null && toDate != null)
            return bookRepository.findByPubDateLessThanEqualOrderByPubDateDesc(toDate);
        else if(fromDate != null && toDate == null)
            return bookRepository.findByPubDateGreaterThanEqualOrderByPubDateDesc(fromDate);
        return bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(fromDate, toDate);
    }

}
