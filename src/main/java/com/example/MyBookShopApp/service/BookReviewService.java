package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.BookReviewDto;
import com.example.MyBookShopApp.repository.BookReviewRepository;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.BookReviewLike;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Service
public class BookReviewService {

    private final BookstoreUserRegister userRegister;
    private final BookReviewRepository bookReviewRepository;
    private final BookRateService bookRateService;
    private final BookReviewLikeService bookReviewLikeService;

    @Autowired
    public BookReviewService(BookReviewRepository bookReviewRepository, BookstoreUserRegister userRegister,
                             BookRateService bookRateService, BookReviewLikeService bookReviewLikeService) {
        this.bookReviewRepository = bookReviewRepository;
        this.userRegister = userRegister;
        this.bookRateService = bookRateService;
        this.bookReviewLikeService = bookReviewLikeService;
    }

    public ResultResponse saveBookReview(Book book, String reviewText) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser == null) {
            result.setResult(false);
            result.setError("Только зарегистрированные пользователи могут оставлять отзывы!");
        } else if (book == null) {
            result.setResult(false);
            result.setError("Книга не найдена!");
        } else if (StringUtils.isEmpty(reviewText) || reviewText.length() < 10) {
            result.setResult(false);
            result.setError("Напишите, пожалуйста, более развернутый отзыв");
        } else {
            BookReview bookReview = bookReviewRepository.findBookReviewByBookIdAndUserId(book.getId(), currentUser.getId());
            if (bookReview == null) {
                bookReview = new BookReview();
                bookReview.setBook(book);
                bookReview.setText(reviewText);
                bookReview.setTime(new Date());
                bookReview.setUser(currentUser);
            } else {
                bookReview.setTime(new Date());
                bookReview.setText(reviewText);
            }
            bookReviewRepository.save(bookReview);
            result.setResult(true);
        }
        return result;
    }

    public List<BookReviewDto> getBookReviewData(String bookSlug) {
        if (bookSlug == null)
            return null;
        List<BookReviewDto> reviewDtoList = new ArrayList<>();
        List<BookReview> reviewList = bookReviewRepository.findBookReviewsByBookSlug(bookSlug);
        for (BookReview review : reviewList) {
            BookReviewDto reviewDto = new BookReviewDto();
            reviewDto.setReview(review);
            reviewDto.setRate(bookRateService.getUserBookRate(review.getBook().getId(), review.getUser().getId()));
            reviewDto.setLikedCount(bookReviewLikeService.getCountReviewByValue(review.getId(), 1));
            reviewDto.setDislikedCount(bookReviewLikeService.getCountReviewByValue(review.getId(), -1));
            reviewDtoList.add(reviewDto);
        }
        Collections.sort(reviewDtoList, (r1, r2) -> (r2.getLikedCount() - r2.getDislikedCount()) - (r1.getLikedCount() - r1.getDislikedCount()));
        return reviewDtoList;
    }

    public ResultResponse bookReviewLiked(BookReviewData bookReviewData) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        BookReview bookReview = bookReviewRepository.findBookReviewById(bookReviewData.getReviewid());
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
            BookReviewLike bookReviewLike = bookReviewLikeService.getUserReviewLike(bookReview.getId(), currentUser.getId());
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
            bookReviewLikeService.save(bookReviewLike);
            result.setResult(true);
        }
        return result;
    }

    public BookReview getUserBookReview(Integer bookId) {
        User currentUser = (User) userRegister.getCurrentUser();
        return currentUser == null ? null : bookReviewRepository.findBookReviewByBookIdAndUserId(bookId, currentUser.getId());
    }

}