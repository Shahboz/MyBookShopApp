package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.entity.User;


public class PhoneNumberUserDetails extends BookstoreUserDetails {

    public PhoneNumberUserDetails(User user) {
        super(user);
    }

    @Override
    public String toString() {
        return getUsername();
    }
}