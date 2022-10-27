package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.AuthorBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface AuthorBooksRepository extends JpaRepository<AuthorBooks, Integer> {

    @Query(value = "select ab from AuthorBooks ab inner join Book b on ab.book.id = b.id where b.slug = :bookSlug")
    List<AuthorBooks> findAuthorsBooksByBookSlug(String bookSlug);

}