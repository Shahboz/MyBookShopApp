package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Document;
import com.example.MyBookShopApp.service.DocumentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@Controller
@RequestMapping("/documents")
public class DocumentsController {

    private final DocumentService documentService;

    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @ModelAttribute("documents")
    private List<Document> getDocuments() {
        return documentService.getDocuments();
    }

    @ModelAttribute("document")
    private Document getDocument(@PathVariable(value = "slug", required = false) String slug) {
        return StringUtils.isEmpty(slug) ? null : documentService.getDocumentBySlug(slug);
    }

    @GetMapping("")
    public String documentsPage(@ModelAttribute("documents") List<Document> documentList) {
        return "/documents/index";
    }

    @GetMapping("/{slug}")
    public String documentPage(@ModelAttribute("document") Document document) {
        return "/documents/slug";
    }

}