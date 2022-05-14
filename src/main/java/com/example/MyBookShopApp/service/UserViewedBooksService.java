package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserViewedBooksRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.UserViewedBooks;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;


@Service
public class UserViewedBooksService {

    private Date beginPeriod;
    private Date endPeriod;
    private final UserViewedBooksRepository userViewedBookRepository;

    @Autowired
    public UserViewedBooksService(UserViewedBooksRepository userViewedBookRepository) {
        endPeriod = new Date();
        beginPeriod = DateUtils.truncate(endPeriod, Calendar.MONTH);
        this.userViewedBookRepository = userViewedBookRepository;
    }

    public void setEndPeriod(Date endPeriod) {
        this.endPeriod = endPeriod;
    }

    public void setBeginPeriod(Date beginPeriod) {
        this.beginPeriod = beginPeriod;
    }

    public UserViewedBooks getUserViewedBook(Integer userId, Integer bookId) {
        return userViewedBookRepository.findUserViewedBooksByUserIdAndBookId(userId, bookId);
    }

    public void save(UserViewedBooks viewedBooks) {
        userViewedBookRepository.save(viewedBooks);
    }

    public Page<Book> getPageOfViewedBooks(Integer userId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return userViewedBookRepository.findUserViewedBooksByUserIdAndTimeBetween(userId, beginPeriod, endPeriod, nextPage);
    }

    public Integer getCountOfViewedBooks(Integer userId) {
        return userViewedBookRepository.countUserViewedBooksByUserIdAndTimeBetween(userId, beginPeriod, endPeriod);
    }

}