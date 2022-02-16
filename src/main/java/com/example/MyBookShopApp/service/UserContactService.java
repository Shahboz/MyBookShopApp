package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserContactRepository;
import com.example.MyBookShopApp.entity.UserContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserContactService {

    private final UserContactRepository userContactRepository;

    @Autowired
    public UserContactService(UserContactRepository userContactRepository) {
        this.userContactRepository = userContactRepository;
    }

    public void save(UserContact userContact) {
        this.userContactRepository.save(userContact);
    }

    public List<UserContact> getUserContacts(Integer userId) {
        return this.userContactRepository.findUserContactsByUserId(userId);
    }

}