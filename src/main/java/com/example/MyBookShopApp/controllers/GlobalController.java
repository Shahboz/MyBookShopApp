package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
public class GlobalController {

    private final BookstoreUserRegister userRegister;

    @Autowired
    public GlobalController(BookstoreUserRegister userRegister) {
        this.userRegister = userRegister;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        return (User) userRegister.getCurrentUser();
    }

    @ModelAttribute("isAnonymousUser")
    public Boolean isAnonymousUser() {
        return userRegister.getCurrentUser() == null;
    }

}