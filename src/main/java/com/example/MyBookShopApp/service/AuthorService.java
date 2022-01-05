package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.AuthorRepository;
import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        Map<String, List<Author>> listMap = authorRepository.findAll().stream().collect(Collectors.groupingBy((Author a) -> a.getName().substring(0, 1)));
        return new TreeMap<>(listMap);
    }

    public Author getAuthorBySlug(String slug) {
        return authorRepository.findBySlug(slug);
    }

    public Page<Book> getPageOfAuthorBooks(String authorSlug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return authorRepository.findBooksBySlug(authorSlug, nextPage);
    }

}
