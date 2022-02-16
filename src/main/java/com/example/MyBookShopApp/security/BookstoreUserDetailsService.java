package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public BookstoreUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(s);
        if(user != null) {
            return new BookstoreUserDetails(user);
        } else {
            System.out.println("s = " + s + ", User not found!");
            System.out.println("Printing stack trace:");
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            for (int i = 1; i < elements.length; i++) {
                StackTraceElement s1 = elements[i];
                System.out.println("\tat " + s1.getClassName() + "." + s1.getMethodName() + "(" + s1.getFileName() + ":" + s1.getLineNumber() + ")");
            }
            throw new UsernameNotFoundException("User not found!");
        }
    }

}