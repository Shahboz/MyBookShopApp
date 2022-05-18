package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.BookReviewRepository;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;


@Service
public class BookReviewService {

    private final BookstoreUserRegister userRegister;
    private final BookService bookService;
    private final BookReviewRepository bookReviewRepository;

    @Autowired
    public BookReviewService(BookReviewRepository bookReviewRepository, BookstoreUserRegister userRegister, BookService bookService) {
        this.bookReviewRepository = bookReviewRepository;
        this.userRegister = userRegister;
        this.bookService = bookService;
    }

    public ResultResponse saveBookReview(BookReviewData bookReviewData) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        Book book = bookService.getBookById(bookReviewData.getBookId());
        if (currentUser == null) {
            result.setResult(false);
            result.setError("Только зарегистрированные пользователи могут оставлять отзывы!");
        } else if (book == null) {
            result.setResult(false);
            result.setError("Не найдена книга с кодом " + bookReviewData.getBookId());
        } else if (StringUtils.isEmpty(bookReviewData.getText()) || bookReviewData.getText().length() < 10) {
            result.setResult(false);
            result.setError("Напишите, пожалуйста, более развернутый отзыв");
        } else {
            BookReview bookReview = this.getUserBookReview(book.getId(), currentUser.getId());
            if (bookReview == null) {
                bookReview = new BookReview();
                bookReview.setBook(book);
                bookReview.setText(bookReviewData.getText());
                bookReview.setTime(new Date());
                bookReview.setUser(currentUser);
            } else {
                bookReview.setTime(new Date());
                bookReview.setText(bookReviewData.getText());
            }
            this.bookReviewRepository.save(bookReview);
            result.setResult(true);
        }
        return result;
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