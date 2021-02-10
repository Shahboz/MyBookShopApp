package com.example.MyBookShopApp.controllers;


import com.example.MyBookShopApp.data.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping(value = "/authors")
public class AuthorsPageController {

    private BookService bookService;

    @Autowired
    public AuthorsPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/author")
    public String author(Model model, @RequestParam(value = "id") Integer authorId) {
        model.addAttribute("author", bookService.getAuthor(authorId));
        model.addAttribute("bookList", bookService.getAuthorBooks(authorId));
        return "authors/slug";
    }

}
