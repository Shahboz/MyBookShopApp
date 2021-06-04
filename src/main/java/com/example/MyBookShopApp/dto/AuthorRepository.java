package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorRepository extends JpaRepository<Author, Integer> {

}
