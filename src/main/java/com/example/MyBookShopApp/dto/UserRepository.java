package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(Integer userId);

}