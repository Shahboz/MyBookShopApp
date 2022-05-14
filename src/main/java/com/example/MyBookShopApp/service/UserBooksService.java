package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserBookTypeRepository;
import com.example.MyBookShopApp.dto.UserBooksRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.UserBookType;
import com.example.MyBookShopApp.entity.UserBooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserBooksService {

    private final UserBooksRepository userBooksRepository;
    private final UserBookTypeRepository bookTypeRepository;

    @Autowired
    public UserBooksService(UserBooksRepository userBooksRepository, UserBookTypeRepository bookTypeRepository) {
        this.userBooksRepository = userBooksRepository;
        this.bookTypeRepository = bookTypeRepository;
    }

    public void save(UserBooks userBooks) {
        userBooksRepository.save(userBooks);
    }

    public UserBookType getUserBookType(String code) {
        return bookTypeRepository.findUserBookTypeByCode(code);
    }

    public void delete(UserBooks userBooks) {
        userBooksRepository.delete(userBooks);
    }

    public UserBooks getUserBookByType(Integer userId, Integer bookId, String userBookTypeCode) {
        return userBooksRepository.findUserBooksByUserAndBookAndUserBookType(userId, bookId, userBookTypeCode);
    }

    public List<Book> getUserBooksByUserBookType(Integer userId, String userBookTypeCode) {
        return userBooksRepository.findUserBooksByUserBookType(userId, userBookTypeCode);
    }

    public Integer getCountUserBooks(Integer userId) {
        return userBooksRepository.countUserBooksByUserId(userId);
    }

}