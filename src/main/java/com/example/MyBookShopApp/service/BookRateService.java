package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.BookRateRepository;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookRate;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.Rate;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
public class BookRateService {

    private final BookRateRepository bookRateRepository;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public BookRateService(BookRateRepository bookRateRepository, BookstoreUserRegister userRegister) {
        this.bookRateRepository = bookRateRepository;
        this.userRegister = userRegister;
    }

    public Rate[] getBookRates(String bookSlug) {
        if (bookSlug == null)
            return null;
        Rate[] rates = Rate.values();
        for (int i = 0; i < rates.length; i++) {
            rates[i].setCount(bookRateRepository.findBookRatesByBookSlugAndValueEquals(bookSlug, rates[i].getValue()).size());
        }
        return rates;
    }

    public Integer getBookRateCount(String bookSlug) {
        return bookRateRepository.countByBookSlug(bookSlug);
    }

    public ResultResponse saveRateBook(Book book, BookReview bookReview, Integer rateValue) {
        ResultResponse result = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        if(currentUser == null) {
            result.setResult(false);
            result.setError("Только зарегистрированные пользователи могут оценить книги!");
        } else if(book == null) {
            result.setResult(false);
            result.setError("Книга не найдена!");
        } else {
            BookRate bookRate = this.getUserBookRate(book.getId(), currentUser.getId());
            if (bookRate == null) {
                bookRate = new BookRate();
                bookRate.setBook(book);
                bookRate.setUser(currentUser);
                bookRate.setTime(new Date());
                bookRate.setValue(rateValue);
                bookRate.setBookReview(bookReview);
            } else {
                bookRate.setTime(new Date());
                bookRate.setValue(rateValue);
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