package com.example.MyBookShopApp.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BookService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBookData() {
        List<Book> books = jdbcTemplate.query("SELECT a.name author_name, b.* FROM books b, author a where b.author_id = a.id", (ResultSet rs, int rownum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author_name"));
            book.setTitle(rs.getString("title"));
            book.setPriceOld(rs.getString("priceOld"));
            book.setPrice(rs.getString("price"));
            return book;
        });
        return new ArrayList<>(books);
    }

    public Map<String, List<String>> getAuthors() {
        HashMap<String, List<String>> listMap = new HashMap<>();
        jdbcTemplate.query("SELECT SUBSTRING(name, 1, 1) key, name FROM author order by name", (ResultSet rs,  int rownum) -> {
            String key = rs.getString("key");
            listMap.putIfAbsent(key, new ArrayList<>());
            listMap.get(key).add(rs.getString("name"));
            return true;
        });
        return listMap;
    }

}
