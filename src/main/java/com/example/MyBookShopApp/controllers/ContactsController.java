package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.dto.UserMessageDto;
import com.example.MyBookShopApp.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/contacts")
public class ContactsController {

    private final UserMessageService messageService;

    @Autowired
    public ContactsController(UserMessageService messageService) {
        this.messageService = messageService;
    }

    @ModelAttribute("appEmail")
    public String getAppEmail() {
        return messageService.getAppEmail();
    }

    @ModelAttribute("appPhone")
    public String getAppPhone() {
        return messageService.getAppPhone();
    }

    @ModelAttribute("appTelegram")
    public String getTelegramBot() {
        return messageService.getTelegramBot();
    }

    @ModelAttribute("userMessageDto")
    public UserMessageDto getUserMessageDto() {
        return new UserMessageDto();
    }

    @GetMapping("")
    public String getContactsPage(@ModelAttribute("appPhone") String appPhone, @ModelAttribute("appEmail") String appEmail, @ModelAttribute("appTelegram") String telegram){
        return "contacts";
    }

    @PostMapping("")
    public String handlePostMessage(@ModelAttribute("userMessageDto") UserMessageDto userMessageDto, Model model) {
        ResultResponse resultResponse = messageService.saveMessage(userMessageDto);
        if (!resultResponse.getResult()) {
            model.addAttribute("errorMessage", resultResponse.getError());
            return "contacts";
        }
        return "redirect:/contacts";
    }

}