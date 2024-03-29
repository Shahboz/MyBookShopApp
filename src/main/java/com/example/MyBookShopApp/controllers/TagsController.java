package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.Tag;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@Controller
@RequestMapping("/tags")
public class TagsController {

    private TagService tagService;
    private BookService bookService;

    @Autowired
    public TagsController(TagService service, BookService bookService) {
        this.tagService = service;
        this.bookService = bookService;
    }

    @ModelAttribute("tag")
    public Tag getGenre(@PathVariable(value = "slug", required = false) String slugTag) {
        return slugTag == null ? null : tagService.getTagBySlug(slugTag);
    }

    @ModelAttribute("books")
    public List<Book> getBooks(@PathVariable(value = "slug", required = false) String slugTag) {
        return slugTag == null ? null : bookService.getPageOfTagBooks(slugTag, 0, 6).getContent();
    }

    @GetMapping(value = {"/", "/{slug}"})
    public String geTagPage(@ModelAttribute("tag") Tag tag, @ModelAttribute("books") List<Book> bookList) {
        return "/tags/index";
    }

}