package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.dto.UserMessageDto;
import com.example.MyBookShopApp.repository.UserMessageRepository;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserMessage;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
public class UserMessageService {

    @Value("${appContact.phoneNumber}")
    private String appPhone;

    @Value("${appEmail.email}")
    private String appEmail;

    @Value("${appContact.telegram.bot}")
    private String telegramBot;

    private final UserMessageRepository userMessageRepository;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public UserMessageService(UserMessageRepository userMessageRepository, BookstoreUserRegister userRegister) {
        this.userMessageRepository = userMessageRepository;
        this.userRegister = userRegister;
    }

    public String getAppPhone() {
        return appPhone;
    }

    public String getAppEmail() {
        return appEmail;
    }

    public String getTelegramBot() {
        return telegramBot;
    }

    public ResultResponse saveMessage(UserMessageDto userMessage) {
        ResultResponse resultResponse = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        if (StringUtils.isEmpty(userMessage.getTopic())) {
            resultResponse.setResult(false);
            resultResponse.setError("Не заполнено тема сообщения");
        } else if (StringUtils.isEmpty(userMessage.getMessage())) {
            resultResponse.setResult(false);
            resultResponse.setError("Не заполнен текст сообщения");
        } else if (currentUser == null && StringUtils.isEmpty(userMessage.getName())) {
            resultResponse.setResult(false);
            resultResponse.setError("Не заполнено имя");
        } else if (currentUser == null && StringUtils.isEmpty(userMessage.getMail())) {
            resultResponse.setResult(false);
            resultResponse.setError("Не заполнено Email");
        } else {
            UserMessage message;
            if (currentUser == null) {
                message = userMessageRepository.findUserMessageByEmailAndNameAndSubject(userMessage.getMail(), userMessage.getName(), userMessage.getTopic());
            } else {
                message = userMessageRepository.findUserMessageByUserIdAndSubject(currentUser.getId(), userMessage.getTopic());
            }
            if (message == null) {
                message = new UserMessage();
                message.setSubject(userMessage.getTopic());
                message.setTime(new Date());
                message.setText(userMessage.getMessage());
                if (currentUser == null) {
                    message.setEmail(userMessage.getMail());
                    message.setName(userMessage.getName());
                } else {
                    message.setUser(currentUser);
                }
            } else {
                message.setTime(new Date());
                message.setText(userMessage.getMessage());
            }
            userMessageRepository.save(message);
            resultResponse.setResult(true);
        }
        return resultResponse;
    }

}