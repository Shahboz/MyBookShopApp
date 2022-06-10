package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Genre;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.MyBookShopApp.entity.Book;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/genres")
public class GenresController {

    private final GenreService genreService;
    private final BookService bookService;

    @Autowired
    public GenresController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @ModelAttribute("genres")
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

    @ModelAttribute("genresDeep")
    public Map<Genre, Integer> getGenreDeep() {
        return genreService.getGenreDeep();
    }

    @ModelAttribute("genre")
    public Genre getGenre(@PathVariable(value = "slug", required = false) String genreSlug) {
        return genreSlug == null ? null : genreService.getGenreBySlug(genreSlug);
    }

    @ModelAttribute("genreBooks")
    public List<Book> getBooksByGenre(@PathVariable(value = "slug", required = false) String genreSlug) {
        return genreSlug == null ? null : bookService.getPageOfGenreBooks(genreSlug, 0, bookService.getRefreshLimit()).getContent();
    }

    @GetMapping("")
    public String getGenresPage(@ModelAttribute("genres") List<Genre> genres,
                                @ModelAttribute("genresDeep") Map<Genre, Integer> genresDeep) {
        return "/genres/index";
    }

    @GetMapping("/{slug}")
    public String getGenrePage(@ModelAttribute("genre") Genre genre, @ModelAttribute("genreBooks") List<Book> bookList) {
        return "/genres/slug";
    }

}