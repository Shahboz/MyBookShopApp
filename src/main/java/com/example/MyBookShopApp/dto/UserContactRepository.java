package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface UserContactRepository extends JpaRepository<UserContact, Integer> {

    @Query(value = "select c from User u left join UserContact c on u.id = c.user.id where u.id = :userId")
    List<UserContact> findUserContactsByUserId(Integer userId);

    List<UserContact> findUserContactsByUserIdAndApproved(Integer userId, Integer approved);

    UserContact findUserContactByContact(String contact);

    List<UserContact> findUserContactsByUserIdAndType(Integer userId, String contactType);

    void deleteUserContactsByUserIdAndApproved(Integer userId, Integer approved);

}