package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookMapper;
import com.example.MyBookShopApp.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", new BookMapper());
        return new ArrayList<>(books);
    }

    public List<Book> getPopularBooks() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books where is_popular = 1", new BookMapper());
        return new ArrayList<>(books);
    }

}
