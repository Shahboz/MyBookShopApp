package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.DocumentRepository;
import com.example.MyBookShopApp.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getDocuments() {
        return documentRepository.findAllByOrderBySortIndex();
    }

    public Document getDocumentBySlug(String slug) {
        return documentRepository.findDocumentBySlug(slug);
    }

}