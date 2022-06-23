package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.UserBookType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookTypeRepository extends JpaRepository<UserBookType, Integer> {

    UserBookType findUserBookTypeByCode(String code);

}