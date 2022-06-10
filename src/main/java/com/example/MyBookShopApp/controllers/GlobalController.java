package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.SearchWordDto;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.UserBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;


@ControllerAdvice
public class GlobalController {

    private final BookstoreUserRegister userRegister;
    private final BookService bookService;
    private final UserBooksService userBooksService;

    @Autowired
    public GlobalController(BookstoreUserRegister userRegister, BookService bookService, UserBooksService userBooksService) {
        this.userRegister = userRegister;
        this.bookService = bookService;
        this.userBooksService = userBooksService;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        return (User) userRegister.getCurrentUser();
    }

    @ModelAttribute("isAnonymousUser")
    public Boolean isAnonymousUser() {
        return userRegister.getCurrentUser() == null;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("boughtBooks")
    public List<Book> getBoughtBooks() {
        return userBooksService.getUserPaidBooks();
    }

    @ModelAttribute("keptBooks")
    public List<Book> getKeptBooks() {
        return userBooksService.getUserKeptBooks();
    }

    @ModelAttribute("cartBooks")
    public List<Book> getCartBooks() {
        return userBooksService.getUserCartBooks();
    }

    @ModelAttribute("archivedBooks")
    public List<Book> getArchivedBooks() {
        return userBooksService.getArchivedBooks();
    }

    @ModelAttribute("refreshOffset")
    public Integer getRefreshOffset() {
        return bookService.getRefreshOffset();
    }

    @ModelAttribute("refreshLimit")
    public Integer getRefreshLimit() {
        return bookService.getRefreshLimit();
    }

    @ModelAttribute("searchLimit")
    public Integer getSearchLimit() {
        return bookService.getSearchLimit();
    }

}