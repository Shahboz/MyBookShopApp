package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface UserContactRepository extends JpaRepository<UserContact, Integer> {

    List<UserContact> findUserContactsByUserHashAndApproved(String userHash, Integer approved);

    UserContact findUserContactByContact(String contact);

    List<UserContact> findUserContactsByUserHashAndType(String userHash, String contactType);

    void deleteUserContactsByUserIdAndApproved(Integer userId, Integer approved);

}