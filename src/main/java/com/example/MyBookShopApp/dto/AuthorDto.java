package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorDto {

    private String name;
    private String slug;

    public AuthorDto(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

}