package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.UserMessageDto;
import com.example.MyBookShopApp.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/contacts")
public class ContactsController {

    @Value("${appContact.phoneNumber}")
    private String appPhone;

    @Value("${appEmail.email}")
    private String appEmail;

    @Value("${appContact.telegram.bot}")
    private String telegramBot;

    private final UserMessageService messageService;

    @Autowired
    public ContactsController(UserMessageService messageService) {
        this.messageService = messageService;
    }

    @ModelAttribute("appEmail")
    public String getAppEmail() {
        return appEmail;
    }

    @ModelAttribute("appPhone")
    public String getAppPhone() {
        return appPhone;
    }

    @ModelAttribute("appTelegram")
    public String getTelegramBot() {
        return telegramBot;
    }

    @GetMapping("")
    public String getContactsPage(@ModelAttribute("appPhone") String appPhone, @ModelAttribute("appEmail") String appEmail, @ModelAttribute("appTelegram") String telegram){
        return "contacts";
    }

    @PostMapping("")
    public String handlePostMessage(UserMessageDto userMessageDto) {
        messageService.saveMessage(userMessageDto);
        return "contacts";
    }

}