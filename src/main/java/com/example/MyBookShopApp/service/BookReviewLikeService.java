package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.BookReviewLikeRepository;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.BookReviewLike;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
public class BookReviewLikeService {

    private final BookstoreUserRegister userRegister;
    private final BookReviewService bookReviewService;
    private final BookReviewLikeRepository bookReviewLikeRepository;

    @Autowired
    public BookReviewLikeService(BookstoreUserRegister userRegister, BookReviewService bookReviewService, BookReviewLikeRepository bookReviewLikeRepository) {
        this.userRegister = userRegister;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
    }

    public ResultResponse saveBookReviewLike(BookReviewData bookReviewData) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        BookReview bookReview = bookReviewService.getReviewById(bookReviewData.getReviewid());
        if (currentUser == null) {
            result.setResult(false);
            result.setError("Только зарегистрированные пользователи могут оценить комментарии!");
        } else if (bookReview == null) {
            result.setResult(false);
            result.setError("Не найден отзыв " + bookReviewData.getReviewid());
        } else if (bookReviewData.getValue() != -1 && bookReviewData.getValue() != 1) {
            result.setResult(false);
            result.setError("Некорректное значение " + bookReviewData.getValue());
        } else {
            BookReviewLike bookReviewLike = this.getUserReviewLike(bookReview.getId(), currentUser.getId());
            if (bookReviewLike == null) {
                bookReviewLike = new BookReviewLike();
                bookReviewLike.setReview(bookReview);
                bookReviewLike.setValue(bookReviewData.getValue());
                bookReviewLike.setTime(new Date());
                bookReviewLike.setUser(currentUser);
            } else {
                bookReviewLike.setTime(new Date());
                bookReviewLike.setValue(bookReviewData.getValue());
            }
            this.bookReviewLikeRepository.save(bookReviewLike);
            result.setResult(true);
        }
        return result;
    }

    public Integer getCountReviewByValue(Integer reviewId, Integer value) {
        return bookReviewLikeRepository.countByReviewIdAndValueIs(reviewId, value);
    }

    public BookReviewLike getUserReviewLike(Integer reviewId, Integer userId) {
        return bookReviewLikeRepository.findBookReviewLikeByReviewIdAndUserId(reviewId, userId);
    }

}