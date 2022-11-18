package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.DocumentDto;
import com.example.MyBookShopApp.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @ModelAttribute("documents")
    private List<DocumentDto> getDocuments() {
        return documentService.getDocuments();
    }

    @ModelAttribute("document")
    private DocumentDto getDocument(@PathVariable(value = "slug", required = false) String slug) {
        return documentService.getDocumentBySlug(slug);
    }

    @GetMapping("")
    public String documentsPage(@ModelAttribute("documents") List<DocumentDto> documentList) {
        return "/documents/index";
    }

    @GetMapping("/{slug}")
    public String documentPage(@ModelAttribute("document") DocumentDto document) {
        return "/documents/slug";
    }

}