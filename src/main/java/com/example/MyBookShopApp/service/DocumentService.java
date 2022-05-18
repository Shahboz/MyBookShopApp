package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.DocumentRepository;
import com.example.MyBookShopApp.entity.Document;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }


    public List<Document> getDocuments() {
        return documentRepository.findAllByOrderBySortIndexDesc();
    }

    public Document getDocumentBySlug(String slug) {
        return documentRepository.findDocumentBySlug(slug);
    }

}