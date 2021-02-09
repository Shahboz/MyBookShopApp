package com.example.MyBookShopApp.controllers;


import com.example.MyBookShopApp.data.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
public class MainPageController {

    private final BookService bookService;

    @Autowired
    public MainPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("bookData", bookService.getBookData());
        return "index";
    }

    @GetMapping("/genres")
    public String genres() {
        return "genres/index";
    }

    @GetMapping("/authors")
    public String authors(Model model) {
        model.addAttribute("authors", bookService.getAuthors());
        return "authors/index";
    }

}
