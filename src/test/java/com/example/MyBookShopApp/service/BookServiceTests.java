package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.BookRepository;
import com.example.MyBookShopApp.entity.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookServiceTests {

    private BookService bookService;
    private List<Book> expectedBookList = new ArrayList<>();

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    public BookServiceTests(BookService bookService) {
        this.bookService = bookService;
    }

    @BeforeEach
    public void setup() {
        for (int i = 4; i >= 0; i--) {
            Book book = new Book();
            book.setSlug("Slug" + i);
            expectedBookList.add(book);
        }
    }

    @AfterEach
    public void tearDown() {
        expectedBookList = null;
    }

    @Test
    @WithUserDetails("root@gmail.com")
    public void testGetPageOfRecommendedBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findRecommendBooksByRate(PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfRecommendedBooks(0, 5).getContent();

        assertNotNull(bookList);
        assertTrue(!bookList.isEmpty());
        assertThat(bookList).hasSize(5);
    }

    @Test
    public void testGetPageOfPopularBooks() {

        Mockito.doReturn(new PageImpl<>(expectedBookList))
                .when(bookRepository)
                .findBooksByPopular(PageRequest.of(0, 5));

        List<Book> bookList = bookService.getPageOfPopularBooks(0, 5).getContent();

        assertNotNull(bookList);
        assertTrue(!bookList.isEmpty());
        assertThat(bookList).hasSize(5);
        assertTrue(bookList.get(0).getSlug().contains("4"));
        assertTrue(bookList.get(4).getSlug().contains("0"));
    }

}