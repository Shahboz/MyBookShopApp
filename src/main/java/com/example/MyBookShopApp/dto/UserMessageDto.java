package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UserMessageDto {

    private String name;
    private String mail;
    private String topic;
    private String message;

}