package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;


public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByOrderByPubDateDesc();

    List<Book> findAllByPubDateBetweenOrderByPubDateDesc(Date fromDate, Date toDate);

    List<Book> findByPubDateLessThanEqualOrderByPubDateDesc(Date toDate);

    List<Book> findByPubDateGreaterThanEqualOrderByPubDateDesc(Date fromDate);

    List<Book> findByOrderByIsBestsellerDesc();

}
