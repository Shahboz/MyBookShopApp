package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.BookStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookshopCardControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    public BookshopCardControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void addBookToCart() throws Exception {
        BookStatus bookStatus = new BookStatus();
        bookStatus.setStatus("CART");
        bookStatus.setBooksIds("simpleBook");
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/books/changeBookStatus")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookStatus)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(cookie().exists("anonymousUser"));
    }

    @Test
    public void deleteBookFromCart() throws Exception {
        BookStatus bookStatus = new BookStatus();
        bookStatus.setStatus("UNLINK");
        bookStatus.setBooksIds("simpleBook");
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/books/changeBookStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookStatus)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(cookie().exists("anonymousUser"));
    }

}