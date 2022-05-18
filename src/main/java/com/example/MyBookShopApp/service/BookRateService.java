package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookRateRepository;
import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookRate;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;


@Service
public class BookRateService {

    private final BookRateRepository bookRateRepository;
    private final BookstoreUserRegister userRegister;
    private final BookService bookService;
    private final BookReviewService bookReviewService;

    @Autowired
    public BookRateService(BookRateRepository bookRateRepository, BookstoreUserRegister userRegister, BookService bookService, BookReviewService bookReviewService) {
        this.bookRateRepository = bookRateRepository;
        this.userRegister = userRegister;
        this.bookService = bookService;
        this.bookReviewService = bookReviewService;
    }

    public List<BookRate> getBookRateValueIs(String bookSlug, Integer rateValue) {
        return bookRateRepository.findBookRatesByBookSlugAndValueEquals(bookSlug, rateValue);
    }

    public Integer getBookRateCount(String bookSlug) {
        return bookRateRepository.countByBookSlug(bookSlug);
    }

    public ResultResponse saveRateBook(BookReviewData bookReviewData) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        Book book = bookService.getBookById(bookReviewData.getBookId());
        if(currentUser == null) {
            result.setResult(false);
            result.setError("Только зарегистрированные пользователи могут оценить книги!");
        } else if(book == null) {
            result.setResult(false);
            result.setError("Не найдена книга с кодом " + bookReviewData.getBookId());
        } else {
            BookReview bookReview = bookReviewService.getUserBookReview(book.getId(), currentUser.getId());
            BookRate bookRate = this.getUserBookRate(book.getId(), currentUser.getId());
            if (bookRate == null) {
                bookRate = new BookRate();
                bookRate.setBook(book);
                bookRate.setUser(currentUser);
                bookRate.setTime(new Date());
                bookRate.setValue(bookReviewData.getValue());
                bookRate.setBookReview(bookReview);
            } else {
                bookRate.setTime(new Date());
                bookRate.setValue(bookReviewData.getValue());
            }
            bookRateRepository.save(bookRate);
            result.setResult(true);
        }
        return result;
    }

    public Integer calcBookRate(String bookSlug) {
        return bookRateRepository.calcBookRate(bookSlug);
    }

    public BookRate getUserBookRate(Integer bookId, Integer userId) {
        return bookRateRepository.findBookRateByBookIdAndUserId(bookId, userId);
    }

}