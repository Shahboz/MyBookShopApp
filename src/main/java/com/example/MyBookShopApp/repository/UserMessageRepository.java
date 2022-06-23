package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserMessageRepository extends JpaRepository<UserMessage, Integer> {

    UserMessage findUserMessageByUserIdAndSubject(Integer userId, String subject);

    UserMessage findUserMessageByEmailAndNameAndSubject(String email, String username, String subject);

}