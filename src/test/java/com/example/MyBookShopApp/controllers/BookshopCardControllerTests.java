package com.example.MyBookShopApp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import javax.servlet.http.Cookie;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(post("/books/changeBookStatus/simpleBook"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/simpleBook"))
                .andExpect(cookie().exists("cartContents"))
                .andExpect(cookie().value("cartContents", "simpleBook"));
    }

    @Test
    public void deleteBookFromCart() throws Exception {
        Cookie cookie = new Cookie("cartContents", "simpleBook/simpleBook1/simpleBook2");
        mockMvc.perform(post("/books/changeBookStatus/cart/remove/simpleBook").cookie(cookie))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/cart"))
                .andExpect(cookie().exists(cookie.getName()))
                .andExpect(cookie().value(cookie.getName(), cookie.getValue().replace("simpleBook/", "")));
    }

}