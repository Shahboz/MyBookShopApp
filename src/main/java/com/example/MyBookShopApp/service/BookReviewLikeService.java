package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewLikeRepository;
import com.example.MyBookShopApp.entity.BookReviewLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BookReviewLikeService {

    private BookReviewLikeRepository bookReviewLikeRepository;

    @Autowired
    public BookReviewLikeService(BookReviewLikeRepository bookReviewLikeRepository) {
        this.bookReviewLikeRepository = bookReviewLikeRepository;
    }

    public void save(BookReviewLike bookReviewLike) {
        this.bookReviewLikeRepository.save(bookReviewLike);
    }

    public Integer getCountReviewByValue(Integer reviewId, Integer value) {
        return bookReviewLikeRepository.countByReviewIdAndValueIs(reviewId, value);
    }

    public BookReviewLike getUserReviewLike(Integer reviewId, Integer userId) {
        return bookReviewLikeRepository.findBookReviewLikeByReviewIdAndUserId(reviewId, userId);
    }

}