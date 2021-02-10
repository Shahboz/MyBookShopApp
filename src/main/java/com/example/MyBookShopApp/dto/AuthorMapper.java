package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.data.Author;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthorMapper implements RowMapper<Author> {

    @Override
    public Author mapRow(ResultSet resultSet, int rownum) throws SQLException {
        Author author = new Author();
        author.setId(resultSet.getInt("id"));
        author.setName(resultSet.getString("name"));
        author.setBiography(resultSet.getString("biography"));
        author.setPhoto(resultSet.getString("photo"));
        return author;
    }
}
