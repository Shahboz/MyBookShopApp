package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    List<BookReview> findBookReviewsByBookSlug(String bookSlug);

    BookReview findBookReviewByBookIdAndUserId(Integer bookId, Integer userId);

    BookReview findBookReviewById(Integer reviewId);

}