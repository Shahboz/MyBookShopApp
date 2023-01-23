package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.AuthorBooks;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.repository.AuthorBooksRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthorBooksServiceTest {

    private Book book;
    private Author author;
    private Integer authorBooksId;
    private AuthorBooks authorBook;
    private List<AuthorBooks> authorBooks;
    private AuthorBooksService authorBooksService;

    @MockBean
    private AuthorBooksRepository authorBooksRepository;

    @Autowired
    AuthorBooksServiceTest(AuthorBooksService service) {
        authorBooksService = service;
    }

    @BeforeEach
    void setUp() {
        authorBooksId = 1;

        book = new Book();
        book.setId(2);
        book.setSlug("Book");
        book.setPubDate(new Date());
        book.setTitle("Title");

        author = new Author();
        author.setId(3);
        author.setSlug("Author");
        author.setName("Name");

        authorBook =new AuthorBooks();
        authorBook.setId(authorBooksId);
        authorBook.setBook(book);
        authorBook.setAuthor(author);
        authorBook.setSortIndex(1);

        authorBooks = new ArrayList<>();
        authorBooks.add(authorBook);
    }

    @AfterEach
    void tearDown() {
        book = null;
        author = null;
        authorBook= null;
        authorBooksId = 0;
        authorBooks = null;
    }

    @Test
    void getAuthorsBook() {

        Mockito.doReturn(authorBooks)
                .when(authorBooksRepository)
                .findAuthorsBooksByBookSlug(book.getSlug());

        List<AuthorBooks> authorBooksList = authorBooksService.getAuthorsBook(book.getSlug());

        assertNotNull(authorBooksList);
        assertFalse(authorBooksList.isEmpty());
        assertThat(authorBooksList).hasSize(1);
        assertEquals(authorBooksList.get(0).getId(), this.authorBooksId);
        assertEquals(authorBooksList.get(0).getBook(), this.book);
        assertEquals(authorBooksList.get(0).getAuthor(), this.author);
        assertEquals(1, authorBooksList.get(0).getSortIndex().intValue());
    }

    @Test
    void saveAuthorBook() {

        Mockito.doReturn(authorBooks)
                .when(authorBooksRepository)
                .findAuthorsBooksByBookSlug(book.getSlug());

        authorBooksService.saveAuthorBook(book, author);

        Mockito.verify(authorBooksRepository, Mockito.times(1)).save(Mockito.any(AuthorBooks.class));
    }

    @Test
    void deleteAuthorBook() {

        Mockito.doReturn(authorBook)
                .when(authorBooksRepository)
                .findAuthorBooksById(authorBooksId);

        authorBooksService.deleteAuthorBook(authorBooksId);

        Mockito.verify(authorBooksRepository, Mockito.times(1)).delete(Mockito.any(AuthorBooks.class));
    }

}