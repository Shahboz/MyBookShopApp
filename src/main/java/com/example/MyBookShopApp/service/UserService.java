package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserRepository;
import com.example.MyBookShopApp.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

}