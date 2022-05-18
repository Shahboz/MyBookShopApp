package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.UserViewedBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserViewedBooksRepository extends JpaRepository<UserViewedBooks, Integer> {

    @Query(value = "select vb.book from UserViewedBooks vb where vb.user.id = :userId and vb.time between date_trunc('MONTH', current_timestamp) and current_timestamp order by vb.time desc")
    Page<Book> findUserViewedBooksByUserId(Integer userId, Pageable nextPage);

}