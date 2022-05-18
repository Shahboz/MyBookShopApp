package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.Tag;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;
import java.util.Map;


@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public MainPageController(BookService bookService, TagService tagService, BookstoreUserRegister userRegister) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.userRegister = userRegister;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> getRecommendedBooks(){
        User currentUser = (User) userRegister.getCurrentUser();
        return bookService.getPageOfRecommendedBooks(currentUser == null ? null : currentUser.getId(), 0, 6).getContent();
    }

    @ModelAttribute("newBooks")
    public List<Book> getNewBooks() {
        return bookService.getPageOfNewBooks(0, 6, null, null).getContent();
    }

    @ModelAttribute("popularBooks")
    public List<Book> getPopularBooks() {
        return bookService.getPageOfPopularBooks(0, 6).getContent();
    }

    @ModelAttribute("tagBooks")
    public Map<Tag, Integer> getTagBooks() {
        return tagService.getTags();
    }

    @GetMapping("/")
    public String getMainPage(){
        return "index";
    }

    @GetMapping("/about")
    public String getAboutPage(){
        return "about";
    }

    @GetMapping("/faq")
    public String getFaqPage(){
        return "faq";
    }

}