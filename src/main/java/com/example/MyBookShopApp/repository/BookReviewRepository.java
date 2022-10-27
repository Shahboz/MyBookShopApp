package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface BookReviewRepository extends JpaRepository<BookReview, Integer> {

    List<BookReview> findBookReviewsByBookSlug(String bookSlug);

    List<BookReview> findBookReviewsByUserHash(String userHash);

    BookReview findBookReviewByBookIdAndUserId(Integer bookId, Integer userId);

    BookReview findBookReviewById(Integer reviewId);

}