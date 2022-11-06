package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.UserBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface UserBooksRepository extends JpaRepository<UserBooks, Integer> {

    @Query(value = "from UserBooks ub where ub.user.id = :userId and ub.book.slug = :bookSlug and ub.type.code = :bookTypeCode")
    UserBooks findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(Integer userId, String bookSlug, String bookTypeCode);

    @Query(value = "select ub.book from UserBooks ub where ub.user.hash = :userHash and ub.type.code = :bookTypeCode")
    List<Book> findUserBooksByUserBookType(String userHash, String bookTypeCode);

}