package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UserProfileDto {

    private String name;
    private String mail;
    private String phone;
    private String password;
    private String passwordReply;

}