package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserBookTypeRepository;
import com.example.MyBookShopApp.entity.UserBookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserBookTypeService {

    private final UserBookTypeRepository bookTypeRepository;

    @Autowired
    public UserBookTypeService(UserBookTypeRepository bookTypeRepository) {
        this.bookTypeRepository = bookTypeRepository;
    }

    public UserBookType getUserBookType(String code) {
        return bookTypeRepository.findUserBookTypeByCode(code);
    }

}