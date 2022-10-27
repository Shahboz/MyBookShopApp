package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findAuthorBySlug(String slug);

    List<Author> findByOrderByName();

}