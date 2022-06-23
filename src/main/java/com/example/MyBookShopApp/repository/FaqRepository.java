package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface FaqRepository extends JpaRepository<Faq, Integer> {

    List<Faq> findAllByOrderBySortIndex();

}