package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewRepository;
import com.example.MyBookShopApp.entity.BookReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class BookReviewService {

    private BookReviewRepository bookReviewRepository;

    @Autowired
    public BookReviewService(BookReviewRepository bookReviewRepository) {
        this.bookReviewRepository = bookReviewRepository;
    }

    public void save(BookReview bookReview) {
        this.bookReviewRepository.save(bookReview);
    }

    public List<BookReview> getBookReview(String bookSlug) {
        return bookReviewRepository.findBookReviewsByBookSlug(bookSlug);
    }

    public BookReview getUserBookReview(Integer bookId, Integer userId) {
        return bookReviewRepository.findBookReviewByBookIdAndUserId(bookId, userId);
    }

    public BookReview getReviewById(Integer reviewId) {
        return bookReviewRepository.findBookReviewById(reviewId);
    }

}