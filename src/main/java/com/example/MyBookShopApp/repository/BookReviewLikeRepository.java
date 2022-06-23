package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.BookReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookReviewLikeRepository extends JpaRepository<BookReviewLike, Integer> {

    Integer countByReviewIdAndValueIs(Integer reviewId, Integer value);

    BookReviewLike findBookReviewLikeByReviewIdAndUserId(Integer reviewId, Integer userId);

}