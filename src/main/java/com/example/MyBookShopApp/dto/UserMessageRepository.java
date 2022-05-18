package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserMessageRepository extends JpaRepository<UserMessage, Integer> {

}