package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.GenreDto;
import com.example.MyBookShopApp.entity.Genre;
import com.example.MyBookShopApp.repository.GenreRepository;
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
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class GenreServiceTest {

    private Integer genreId;
    private String genreSlug;
    private String genreName;
    private Genre genre;
    private List<Genre> expectedGenreList;

    @MockBean
    private GenreRepository genreRepository;

    private GenreService genreService;

    @Autowired
    GenreServiceTest(GenreService genreService) {
        this.genreService = genreService;
    }

    @BeforeEach
    void setUp() {
        genreId = 1;
        genreSlug = "Slug";
        genreName = "Name";
        genre = new Genre(genreSlug, genreName);
        genre.setId(genreId);

        expectedGenreList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Genre genre1 = new Genre("Slug" + i, "Name" + i);
            genre1.setParent(this.genre);
            expectedGenreList.add(genre1);
        }
    }

    @AfterEach
    void tearDown() {
        genreId = 0;
        genreSlug = null;
        genreName = null;
        genre = null;
        expectedGenreList = null;
    }

    @Test
    void getGenres() {

        expectedGenreList.forEach(genre1 -> genre1.setParent(null));

        Mockito.doReturn(expectedGenreList)
                .when(genreRepository)
                .findAll();

        List<Genre> genreList = genreService.getGenres();

        assertNotNull(genreList);
        assertFalse(genreList.isEmpty());
        assertThat(genreList).hasSize(2);
        assertTrue(genreList.get(0).getSlug().contains("1"));
        assertTrue(genreList.get(1).getSlug().contains("2"));
    }

    @Test
    void getGenreDeep() {

        Mockito.doReturn(expectedGenreList)
                .when(genreRepository)
                .findAll();

        Mockito.doReturn(1)
                .when(genreRepository)
                .findDeepByGenre(genreId);

        Map<Genre, Integer> genresMap = genreService.getGenreDeep();

        assertNotNull(genresMap);
        assertThat(genresMap).hasSize(2);
    }

    @Test
    void getGenreBySlug() {

        Mockito.doReturn(genre)
                .when(genreRepository)
                .findGenreBySlug(genreSlug);

        GenreDto dto = genreService.getGenreBySlug(genreSlug);

        assertNotNull(dto);
        assertEquals(dto.getSlug(), genre.getSlug());
        assertEquals(dto.getName(), genre.getName());
    }

    @Test
    void testGetEmptyGenreBySlug() {
        GenreDto dto = genreService.getGenreBySlug("");

        assertNull(dto);
    }

}