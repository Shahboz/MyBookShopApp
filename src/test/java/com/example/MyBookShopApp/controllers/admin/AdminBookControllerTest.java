package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.utils.DateFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AdminBookControllerTest {

    private BookInfoDto bookInfoDto;

    private final MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    AdminBookControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
        bookInfoDto = new BookInfoDto();
        bookInfoDto.setTitle("Title test");
        bookInfoDto.setSlug("Slug test");
        bookInfoDto.setPubDate(DateFormatter.format(new Date()));
        bookInfoDto.setPrice(10);
        bookInfoDto.setDiscount(30);
        bookInfoDto.setImage("Image test");
        bookInfoDto.setDescription("Description test");
        bookInfoDto.setIsBestseller(true);
    }

    @AfterEach
    void tearDown() {
        bookInfoDto = null;
    }

    @Test
    void addBookSuccessTest() throws Exception {

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", bookInfoDto.getTitle());
        requestParams.add("slug", bookInfoDto.getSlug());
        requestParams.add("pubDate", bookInfoDto.getPubDate());
        requestParams.add("price", String.valueOf(bookInfoDto.getPrice()));
        requestParams.add("discount", String.valueOf(bookInfoDto.getPrice()));
        requestParams.add("image", bookInfoDto.getImage());
        requestParams.add("description", bookInfoDto.getDescription());
        requestParams.add("isBestseller", String.valueOf(bookInfoDto.getIsBestseller()));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/admin/book/add")
                .params(requestParams))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books"));

        Mockito.verify(bookRepository, Mockito.times(1)).save(Mockito.any(Book.class));
    }

}