package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.dto.AuthorMapper;
import com.example.MyBookShopApp.dto.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookService {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBookData() {
        List<Book> books = jdbcTemplate.query("SELECT a.name author_name, b.* FROM books b, author a where b.author_id = a.id", new BookMapper());
        return new ArrayList<>(books);
    }

    public Map<String, List<Author>> getAuthors() {
        List<Author> authors =  jdbcTemplate.query("SELECT id, name, biography, photo FROM author order by name", new AuthorMapper());
        return authors.stream().collect(Collectors.groupingBy(author -> author.getName().substring(0, 1)));
    }

    public Author getAuthor(Integer authorId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("authorId", authorId);
        return jdbcTemplate.queryForObject("SELECT id, name, biography, photo FROM author WHERE id = :authorId", parameterSource, new AuthorMapper());
    }

    public List<Book> getAuthorBooks(Integer authorId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("authorId", authorId);
        List<Book> books = jdbcTemplate.query("SELECT a.name author_name, b.* FROM books b, author a where b.author_id = a.id and a.id = :authorId", parameterSource, new BookMapper());
        return new ArrayList<>(books);
    }

}
