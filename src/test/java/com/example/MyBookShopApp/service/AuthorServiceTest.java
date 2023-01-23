package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.AuthorDto;
import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.repository.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthorServiceTest {

    private String authorSlug;
    private Author author;
    private List<Author> expectedAuthorList;

    @MockBean
    private AuthorRepository authorRepository;

    private final AuthorService authorService;

    @Autowired
    AuthorServiceTest(AuthorService authorService) {
        this.authorService = authorService;
    }

    @BeforeEach
    void setUp() {
        authorSlug = "Slug";

        author = new Author();
        author.setId(1);
        author.setSlug(authorSlug);
        author.setName("Author Name");
        author.setDescription("Author description");

        expectedAuthorList = new ArrayList<>();
        for (int i = 10; i < 15; i++) {
            Author author1 = new Author();
            author1.setId(i);
            author1.setSlug("AuthorSlug" + i);
            author1.setName(String.valueOf((char)(55 + i)));

            expectedAuthorList.add(author1);
        }
    }

    @AfterEach
    void tearDown() {
        authorSlug = null;
        author = null;
        expectedAuthorList = null;
    }

    @Test
    void testGetAuthorsMap() {

        Mockito.doReturn(expectedAuthorList)
                .when(authorRepository)
                .findAll();

        Map<String, List<Author>> listMap = authorService.getAuthorsMap();

        assertNotNull(listMap);
        assertThat(listMap).hasSize(5);
        assertThat(listMap.get("A")).hasSize(1);
        assertThat(listMap.get("B")).hasSize(1);
        assertThat(listMap.get("C")).hasSize(1);
        assertThat(listMap.get("D")).hasSize(1);
        assertThat(listMap.get("E")).hasSize(1);
    }

    @Test
    void getAuthorBySlug() {

        Mockito.doReturn(author)
                .when(authorRepository)
                .findAuthorBySlug(authorSlug);

        Author author1 = authorService.getAuthorBySlug(authorSlug);

        assertNotNull(author1);
        assertEquals(author1.getId(), author.getId());
        assertEquals(author1.getSlug(), authorSlug);
        assertEquals(author1.getName(), author.getName());
    }

    @Test
    void testGetAuthorDto() {

        Mockito.doReturn(author)
                .when(authorRepository)
                .findAuthorBySlug(authorSlug);

        AuthorDto authorDto = authorService.getAuthorDto(authorSlug);

        assertNotNull(authorDto);
        assertEquals(authorDto.getSlug(), authorSlug);
        assertEquals(authorDto.getName(), author.getName());
    }

    @Test
    void testEmptyGetAuthorDto() {

        AuthorDto authorDto = authorService.getAuthorDto(null);

        assertNull(authorDto);
    }

    @Test
    void testAddNewAuthor() {

        Mockito.doReturn(null)
                .when(authorRepository)
                .findAuthorBySlug(authorSlug);

        authorService.saveAuthor(new AuthorDto(author));

        Mockito.verify(authorRepository, Mockito.times(1)).save(Mockito.any(Author.class));
    }

    @Test
    void testUpdateAuthor() {

        Mockito.doReturn(author)
                .when(authorRepository)
                .findAuthorBySlug(authorSlug);

        AuthorDto dto = new AuthorDto(author);
        dto.setName("Author name is updated");
        dto.setDescription("Author description is updated");

        authorService.saveAuthor(dto);

        Mockito.verify(authorRepository, Mockito.times(1)).save(Mockito.any(Author.class));
    }

}