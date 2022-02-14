package com.example.MyBookShopApp.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactConfirmationResponse {

    private String result;

    public ContactConfirmationResponse(String result) {
        this.result = result;
    }

}