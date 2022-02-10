package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookFileRepository extends JpaRepository<BookFile, Integer> {

    BookFile findBookFileByHash(String hash);

}