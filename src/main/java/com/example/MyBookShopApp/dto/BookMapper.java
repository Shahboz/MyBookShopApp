package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet resultSet, int i) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getInt("id"));
        book.setAuthor(resultSet.getString("author"));
        book.setTitle(resultSet.getString("title"));
        book.setPriceOld(resultSet.getString("priceOld"));
        book.setPrice(resultSet.getString("price"));
        book.setSignPopular(resultSet.getInt("is_popular"));
        return book;
    }
}
