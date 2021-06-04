package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.service.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
public class AuthorsController {

    private final AuthorService authorService;

    @Autowired
    public AuthorsController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<Author>> authorsMap() {
        return authorService.getAuthorsMap();
    }

    @ModelAttribute("author")
    public Author getAuthor(@RequestParam(value = "authorId", required = false, defaultValue = "0") int authorId) {
        if (authorId == 0) {
            return null;
        }
        return authorService.getAuthorById(authorId);
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "/authors/index";
    }

    @GetMapping("/authors/slug")
    public String authorPage(@ModelAttribute("author") Author author, Model model) {
        return "/authors/slug";
    }

    @GetMapping("/books/author")
    public String authorBookPage(@ModelAttribute("author") Author author, Model model) {
        return "/books/author";
    }

}
