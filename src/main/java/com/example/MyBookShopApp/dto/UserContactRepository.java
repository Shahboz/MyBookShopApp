package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface UserContactRepository extends JpaRepository<UserContact, Integer> {

    List<UserContact> findUserContactsByUserId(Integer userId);

}