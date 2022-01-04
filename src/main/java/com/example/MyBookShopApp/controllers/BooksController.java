package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;

    @Autowired
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("popularBooks")
    public List<Book> getPopularBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit){
        return bookService.getPageOfPopularBooks(offset, limit).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<Book> getRecentBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        return bookService.getPageOfNewBooks(offset, limit, null, null).getContent();
    }

    @ModelAttribute("authorBooks")
    public List<Book> getAuthorBooks(@PathVariable(value = "slug", required = false) String authorSlug,
                                     @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        if (authorSlug == null)
            return null;
        return bookService.getPageOfAuthorBooks(authorSlug, offset, limit).getContent();
    }

    @ModelAttribute("author")
    public Author getAuthor(@PathVariable(value = "slug", required = false) String authorSlug) {
        if (authorSlug == null)
            return null;
        return bookService.getAuthorBySlug(authorSlug);
    }

    @GetMapping(value = "/recommended", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getRecommendedBooksByPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }

    @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getRecentBooksByPage(@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                             @RequestParam(value = "to",   required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate,
                                             @RequestParam("offset") Integer offset,
                                             @RequestParam("limit")  Integer limit
    ) {
        return new BooksPageDto(bookService.getPageOfNewBooks(offset, limit, fromDate, toDate).getContent());
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getPopularBooksByPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }

    @GetMapping(value = "/author/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getAuthorBooksByPage(@PathVariable("slug") String authorSlug, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfAuthorBooks(authorSlug, offset, limit).getContent());
    }

    @GetMapping(value = "/genre/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getGenreBooksByPage(@PathVariable("slug") String genreSlug, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfGenreBooks(genreSlug, offset, limit).getContent());
    }

    @GetMapping(value = "/tag/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getTagBooksByPage(@PathVariable("slug") String tagSlug, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfTagBooks(tagSlug, offset, limit).getContent());
    }

    @GetMapping(value = "/recent", produces = MediaType.TEXT_HTML_VALUE)
    public String getRecentPage(@ModelAttribute("recentBooks") List<Book> recentBooks) {
        return "/books/recent";
    }

    @GetMapping(value = "/popular", produces = MediaType.TEXT_HTML_VALUE)
    public String getPopularPage(@ModelAttribute("popularBooks") List<Book> popularBooks) {
        return "/books/popular";
    }

    @GetMapping(value = "/author/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getAuthorBooks(@ModelAttribute("authorBooks") List<Book> authorBooks,
                                 @ModelAttribute("author") Author author) {
        return "/books/author";
    }
}
