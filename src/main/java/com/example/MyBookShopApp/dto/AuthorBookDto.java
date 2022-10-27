package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AuthorBookDto {

    private String bookSlug;
    private String authorSlug;

}