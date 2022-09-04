package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.Role;
import com.example.MyBookShopApp.repository.RoleRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    public Role getUserRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName);
    }

}