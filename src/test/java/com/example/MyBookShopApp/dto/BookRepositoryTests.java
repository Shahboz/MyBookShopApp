package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTests {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTests(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void findBooksByTitleContaining() {
        String title ="of";
        List<Book> bookListByTitle = bookRepository.findBooksByTitleContaining(title);

        assertNotNull(bookListByTitle);
        assertFalse(bookListByTitle.isEmpty());

        for (Book book : bookListByTitle) {
            assertThat(book.getTitle()).contains(title);
        }
    }

    @Test
    void getBestsellers() {
        List<Book> bookListBestsellers = bookRepository.getBestsellers();

        assertNotNull(bookListBestsellers);
        assertFalse(bookListBestsellers.isEmpty());
        assertThat(bookListBestsellers).hasSizeGreaterThan(1);
    }

    @Test
    void findBooksByPriceBetween() {
        Integer minPrice = 700, maxPrice = 800;
        List<Book> bookListPriceBetween = bookRepository.findBooksByPriceBetween(minPrice, maxPrice);

        assertNotNull(bookListPriceBetween);
        assertFalse(bookListPriceBetween.isEmpty());

        for (Book book : bookListPriceBetween) {
            assertThat(book.getPrice()).isBetween(minPrice, maxPrice);
        }
    }
}