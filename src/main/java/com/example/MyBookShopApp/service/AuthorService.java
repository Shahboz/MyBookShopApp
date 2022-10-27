package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.AuthorBooks;
import com.example.MyBookShopApp.repository.AuthorBooksRepository;
import com.example.MyBookShopApp.repository.AuthorRepository;
import com.example.MyBookShopApp.entity.Author;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorBooksRepository authorBooksRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, AuthorBooksRepository authorBooksRepository) {
        this.authorRepository = authorRepository;
        this.authorBooksRepository = authorBooksRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
        Map<String, List<Author>> listMap = authorRepository.findAll().stream().collect(Collectors.groupingBy((Author a) -> a.getName().substring(0, 1)));
        return new TreeMap<>(listMap);
    }

    public Author getAuthorBySlug(String slug) {
        return authorRepository.findAuthorBySlug(slug);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findByOrderByName();
    }

    public List<AuthorBooks> getAuthorsBook(String bookSlug) {
        return authorBooksRepository.findAuthorsBooksByBookSlug(bookSlug);
    }

    public AuthorBooks getAuthorBooks(Integer authorBookId) {
        return authorBooksRepository.getOne(authorBookId);
    }

    public void deleteAuthorBooks(AuthorBooks authorBook) {
        authorBooksRepository.delete(authorBook);
    }

    public void saveAuthor(Author authorDto) {
        Author author = authorRepository.findAuthorBySlug(authorDto.getSlug());
        if (StringUtils.isEmpty(authorDto.getSlug()) || author == null) {
            // Создание автора
            author = new Author();
            author.setName(authorDto.getName());
            author.setDescription(authorDto.getDescription());
            author.setPhoto(authorDto.getPhoto());
            author.setSlug(author.getName().replaceAll("[^a-z0-9A-Z]", ""));
        } else {
            // Обновление ФИО автора
            if (!StringUtils.isEmpty(authorDto.getName()) && !author.getName().equals(authorDto.getName())) {
                author.setName(authorDto.getName());
            }
            // Обновление биографии
            if (!author.getDescription().equals(authorDto.getDescription())) {
                author.setDescription(authorDto.getDescription());
            }
        }
        // Сохранение
        authorRepository.save(author);
    }

    public void saveAuthorBook(AuthorBooks authorBooks) {
        authorBooksRepository.save(authorBooks);
    }

}