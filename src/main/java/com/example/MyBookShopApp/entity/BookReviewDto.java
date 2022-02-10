package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookReviewDto {

    private BookReview review;
    private BookRate rate;
    private Integer likedCount;
    private Integer dislikedCount;

}