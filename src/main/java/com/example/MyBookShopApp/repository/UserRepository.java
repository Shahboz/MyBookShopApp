package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select u from User u inner join UserContact c on u.id = c.user.id where c.type = 'EMAIL' and c.contact = :email")
    User findUserByEmail(String email);

    @Query(value = "select u from User u inner join UserContact c on u.id = c.user.id where c.type = 'PHONE' and c.contact = :phone")
    User findUserByPhone(String phone);

    User findUserByHash(String hash);

    User findUserById(Integer id);

}