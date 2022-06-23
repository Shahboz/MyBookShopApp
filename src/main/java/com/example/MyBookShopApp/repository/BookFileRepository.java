package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface BookFileRepository extends JpaRepository<BookFile, Integer> {

    BookFile findFileByHash(String hash);

    List<BookFile> findAllByBookSlugOrderByTypeNameAsc(String bookSlug);

    Book findBookByHash(String hash);

}