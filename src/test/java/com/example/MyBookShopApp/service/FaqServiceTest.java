package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.Faq;
import com.example.MyBookShopApp.repository.FaqRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FaqServiceTest {

    private FaqService faqService;
    private List<Faq> expectedFaqList = new ArrayList<>();

    @MockBean
    private FaqRepository faqRepository;

    @Autowired
    FaqServiceTest(FaqService faqService) {
        this.faqService = faqService;
    }

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 2; i++) {
            Faq faq = new Faq();
            faq.setId(i);
            faq.setSortIndex(i);
            faq.setQuestion("Is test " + i + "?");
            faq.setAnswer("Yes. It is the " + i + " test.");
            expectedFaqList.add(faq);
        }
    }

    @AfterEach
    void tearDown() {
        expectedFaqList = null;
    }

    @Test
    void getAllFaqs() {

        Mockito.doReturn(expectedFaqList)
                .when(faqRepository)
                .findAllByOrderBySortIndex();

        List<Faq> faqList = faqService.getAllFaqs();

        assertNotNull(faqList);
        assertFalse(faqList.isEmpty());
        assertThat(faqList).hasSize(2);
        assertEquals(1, faqList.get(0).getSortIndex().intValue());
        assertEquals(2, faqList.get(1).getSortIndex().intValue());
    }

}