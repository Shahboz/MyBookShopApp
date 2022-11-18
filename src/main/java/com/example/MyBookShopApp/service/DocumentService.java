package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.DocumentDto;
import com.example.MyBookShopApp.repository.DocumentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentDto> getDocuments() {
        return documentRepository.findAllByOrderBySortIndex().stream().map(document -> new DocumentDto(document)).collect(Collectors.toList());
    }

    public DocumentDto getDocumentBySlug(String slug) {
        return StringUtils.isEmpty(slug) ? null : new DocumentDto(documentRepository.findDocumentBySlug(slug));
    }

}