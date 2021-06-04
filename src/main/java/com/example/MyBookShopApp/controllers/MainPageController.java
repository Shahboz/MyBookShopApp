package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;

    @Autowired
    public MainPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks(){
        return bookService.getBooksData();
    }

    @ModelAttribute("newBooks")
    public List<Book> newBooks() {
        return bookService.getNewBooks(null, null);
    }

    @ModelAttribute("postponedBooks")
    public List<Book> postponedBooks() {
        List<Book> bookList = bookService.getBooksData();
        if(bookList.isEmpty() || bookList.size() < 3) return null;
        return bookService.getBooksData().subList(0, 3);
    }

    @GetMapping("/")
    public String mainPage(){
        return "index";
    }

    @GetMapping("/postponed")
    public String postponedPage(){
        return "postponed";
    }

    @GetMapping("/cart")
    public String cartPage(){
        return "cart";
    }

    @GetMapping("/signin")
    public String signinPage(){
        return "signin";
    }

    @GetMapping("/signup")
    public String signupPage(){
        return "signup";
    }

    @GetMapping("/about")
    public String aboutPage(){
        return "about";
    }

    @GetMapping("/faq")
    public String faqPage(){
        return "faq";
    }

    @GetMapping("/contacts")
    public String contactsPage(){
        return "contacts";
    }

}
