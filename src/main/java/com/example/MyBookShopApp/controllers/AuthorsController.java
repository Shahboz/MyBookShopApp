package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/authors")
@Api(description = "Authors data")
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;

    @Autowired
    public AuthorsController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<Author>> authorsMap() {
        return authorService.getAuthorsMap();
    }

    @ModelAttribute("author")
    public Author author(@PathVariable(value = "slug", required = false) String authorSlug) {
        if(authorSlug == null)
            return null;
        return authorService.getAuthorBySlug(authorSlug);
    }

    @ModelAttribute("authorBooks")
    public List<Book> authorBooks(@PathVariable(value = "slug", required = false) String authorSlug,
                                  @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        if(authorSlug == null)
            return null;
        return bookService.getPageOfAuthorBooks(authorSlug, offset, limit).getContent();
    }

    @GetMapping("")
    public String getAuthorsPage() {
        return "/authors/index";
    }

    @GetMapping("/{slug}")
    public String getAuthorPage(@ModelAttribute("author") Author author, @ModelAttribute("authorBooks") List<Book> authorBooks) {
        return "/authors/slug";
    }

    @ApiOperation("Method to get map authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public Map<String,List<Author>> authors() {
        return authorService.getAuthorsMap();
    }

}