package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select new User(u.id, u.hash, u.regTime, u.balance, u.name, u.email, u.password) from User u inner join UserContact c on u.id = c.user.id " +
            "where c.type = 'EMAIL' and c.contact = :email")
    User findUserByEmail(String email);

    @Query(value = "select new User(u.id, u.hash, u.regTime, u.balance, u.name, u.email, u.password) from User u inner join UserContact c on u.id = c.user.id " +
            "where c.type = 'PHONE' and c.contact = :phone")
    User findUserByPhone(String phone);

    User findUserByHash(String hash);

    User findUserById(Integer id);

}