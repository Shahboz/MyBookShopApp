package com.example.MyBookShopApp.security;

import lombok.Getter;

@Getter
public class ContactConfirmationResponse {

    private String result;

    public ContactConfirmationResponse(String result) {
        this.result = result;
    }

}