package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.UserViewedBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;


public interface UserViewedBooksRepository extends JpaRepository<UserViewedBooks, Integer> {

    UserViewedBooks findUserViewedBooksByUserIdAndBookId(Integer userId, Integer bookId);

    @Query(value = "select vb.book from UserViewedBooks vb where vb.user.id = :userId and vb.time between :beginPeriod and :endPeriod order by vb.time desc")
    Page<Book> findUserViewedBooksByUserIdAndTimeBetween(Integer userId, Date beginPeriod, Date endPeriod, Pageable nextPage);

    Integer countUserViewedBooksByUserIdAndTimeBetween(Integer userId, Date beginPeriod, Date endPeriod);

}