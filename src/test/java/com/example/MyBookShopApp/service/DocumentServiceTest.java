package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.DocumentDto;
import com.example.MyBookShopApp.entity.Document;
import com.example.MyBookShopApp.repository.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class DocumentServiceTest {

    private Document document;
    private List<Document> documentList;

    private DocumentService documentService;

    @MockBean
    private DocumentRepository documentRepository;

    @Autowired
    DocumentServiceTest(DocumentService service) {
        this.documentService = service;
    }

    @BeforeEach
    void setUp() {
        document = new Document();
        document.setId(1);
        document.setSortIndex(1);
        document.setSlug("doc");
        document.setTitle("Title");
        document.setText("It is text document.");

        documentList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Document doc = new Document();
            doc.setSortIndex(i);
            documentList.add(doc);
        }
    }

    @AfterEach
    void tearDown() {
        document = null;
        documentList = null;
    }

    @Test
    void getDocuments() {

        Mockito.doReturn(documentList)
                .when(documentRepository)
                .findAllByOrderBySortIndex();

        List<DocumentDto> dtoList = documentService.getDocuments();

        assertNotNull(dtoList);
        assertFalse(dtoList.isEmpty());
        assertThat(dtoList).hasSize(2);
        assertEquals(1, dtoList.get(0).getSortIndex().intValue());
        assertEquals(2, dtoList.get(1).getSortIndex().intValue());
    }

    @Test
    void getDocumentBySlug() {

        Mockito.doReturn(document)
                .when(documentRepository)
                .findDocumentBySlug(document.getSlug());

        DocumentDto dto = documentService.getDocumentBySlug(document.getSlug());

        assertNotNull(dto);
        assertEquals(dto.getSlug(), this.document.getSlug());
        assertEquals(dto.getTitle(), this.document.getTitle());
        assertEquals(dto.getText(), this.document.getText());
        assertEquals(dto.getSortIndex(), this.document.getSortIndex());
    }

    @Test
    void getEmptyDocumentBySlug() {
        DocumentDto dto = documentService.getDocumentBySlug("");

        assertNull(dto);
    }

}