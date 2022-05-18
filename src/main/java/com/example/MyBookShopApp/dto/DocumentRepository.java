package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<Document> findAllByOrderBySortIndexDesc();

    Document findDocumentBySlug(String slug);

}