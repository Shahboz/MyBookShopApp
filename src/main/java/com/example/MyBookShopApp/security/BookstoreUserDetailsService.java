package com.example.MyBookShopApp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final BookstoreUerRepository bookstoreUerRepository;

    @Autowired
    public BookstoreUserDetailsService(BookstoreUerRepository bookstoreUerRepository) {
        this.bookstoreUerRepository = bookstoreUerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        BookstoreUser bookstoreUser = bookstoreUerRepository.findBookstoreUserByEmail(s);
        if(bookstoreUser != null) {
            return new BookstoreUserDetails(bookstoreUser);
        } else {
            throw new UsernameNotFoundException("User not found doh!");
        }
    }

}