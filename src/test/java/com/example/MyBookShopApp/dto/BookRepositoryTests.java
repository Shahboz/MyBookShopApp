package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import org.jboss.logging.Logger;
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
        String token ="As";
        List<Book> bookListByTitle = bookRepository.findBooksByTitleContaining(token);

        assertNotNull(bookListByTitle);
        assertFalse(bookListByTitle.isEmpty());

        for (Book book : bookListByTitle) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.getTitle());
            assertThat(book.getTitle()).contains(token);
        }
    }

    @Test
    void getBestsellers() {
        List<Book> bookListBestsellers = bookRepository.getBestsellers();

        assertNotNull(bookListBestsellers);
        assertFalse(bookListBestsellers.isEmpty());
        assertThat(bookListBestsellers.size()).isGreaterThan(1);
    }

    @Test
    void findBooksByPriceBetween() {
        Integer minPrice = 705, maxPrice = 710;
        List<Book> bookListPriceBetween = bookRepository.findBooksByPriceBetween(minPrice, maxPrice);

        assertNotNull(bookListPriceBetween);
        assertFalse(bookListPriceBetween.isEmpty());

        for (Book book : bookListPriceBetween) {
            Logger.getLogger(this.getClass().getSimpleName()).info(book.getPrice());
            assertThat(book.getPrice()).isBetween(minPrice, maxPrice);
        }
    }
}