package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserRepository;
import com.example.MyBookShopApp.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Getter
@Setter
@Service
@NoArgsConstructor
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserbyId(Integer userId) {
        return userRepository.findUserById(userId);
    }

}