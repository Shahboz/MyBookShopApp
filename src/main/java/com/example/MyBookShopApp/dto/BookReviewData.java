package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class BookReviewData {

    private Integer bookId;
    private Integer reviewid;
    private Integer value;
    private String text;

}