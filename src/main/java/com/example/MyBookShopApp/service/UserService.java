package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.UserRepository;
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

    public void save(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findUserByPhone(phone);
    }

    public User getUserByHash(String userHash) {
        return userRepository.findUserByHash(userHash);
    }

    public User getUserById(Integer userId) {
        return userRepository.findUserById(userId);
    }

}