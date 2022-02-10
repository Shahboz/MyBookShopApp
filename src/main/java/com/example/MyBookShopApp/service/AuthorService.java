package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.AuthorRepository;
import com.example.MyBookShopApp.entity.Author;
import org.springframework.beans.factory.annotation.Autowired;
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
        return authorRepository.findAuthorBySlug(slug);
    }

}