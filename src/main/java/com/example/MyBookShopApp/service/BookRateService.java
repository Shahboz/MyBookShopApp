package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookRateRepository;
import com.example.MyBookShopApp.entity.BookRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class BookRateService {

    private BookRateRepository bookRateRepository;

    @Autowired
    public BookRateService(BookRateRepository bookRateRepository) {
        this.bookRateRepository = bookRateRepository;
    }

    public List<BookRate> getBookRateValueIs(String bookSlug, Integer rateValue) {
        return bookRateRepository.findBookRatesByBookSlugAndValueEquals(bookSlug, rateValue);
    }

    public Integer getBookRateCount(String bookSlug) {
        return bookRateRepository.countByBookSlug(bookSlug);
    }

    public void save(BookRate bookRate) {
        bookRateRepository.save(bookRate);
    }

    public Integer calcBookRate(String bookSlug) {
        return bookRateRepository.calcBookRate(bookSlug);
    }

    public BookRate getUserBookRate(Integer bookId, Integer userId) {
        return bookRateRepository.findBookRateByBookIdAndUserId(bookId, userId);
    }

}