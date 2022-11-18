package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.AuthorDto;
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

    public Integer getCountAuthors() {
        return Math.toIntExact(authorRepository.count());
    }

    public AuthorDto getAuthorDto(String authorSlug) {
        Author author = StringUtils.isEmpty(authorSlug) ? null : authorRepository.findAuthorBySlug(authorSlug);
        return author == null ? null : new AuthorDto(author);
    }

    public List<AuthorDto> getAllAuthorsDto() {
        return authorRepository.findByOrderByName().stream().map(AuthorDto::new).collect(Collectors.toList());
    }

    public void saveAuthor(AuthorDto authorDto) {
        Boolean isChange = false;
        Author author = authorRepository.findAuthorBySlug(authorDto.getSlug());
        if (author == null) {
            // Создание автора
            author = new Author();
            author.setName(authorDto.getName());
            author.setDescription(authorDto.getDescription());
            author.setPhoto(authorDto.getPhoto());
            author.setSlug(authorDto.getSlug());
            isChange = true;
        } else {
            // Обновление ФИО автора
            if (!StringUtils.isEmpty(authorDto.getName()) && !author.getName().equals(authorDto.getName())) {
                author.setName(authorDto.getName());
                isChange = true;
            }
            // Обновление биографии
            if (!author.getDescription().equals(authorDto.getDescription())) {
                author.setDescription(authorDto.getDescription());
                isChange = true;
            }
        }
        if (isChange) {
            authorRepository.save(author);
        }
    }

}