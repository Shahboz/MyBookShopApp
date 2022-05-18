package com.example.MyBookShopApp.service;


import com.example.MyBookShopApp.dto.UserMessageRepository;
import com.example.MyBookShopApp.entity.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserMessageService {

    private final UserMessageRepository userMessageRepository;

    @Autowired
    public UserMessageService(UserMessageRepository userMessageRepository) {
        this.userMessageRepository = userMessageRepository;
    }

    public void save(UserMessage userMessage) {
        userMessageRepository.save(userMessage);
    }

}