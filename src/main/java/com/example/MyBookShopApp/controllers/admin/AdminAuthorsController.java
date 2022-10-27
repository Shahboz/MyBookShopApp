package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.entity.Author;
import com.example.MyBookShopApp.service.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/admin/authors")
public class AdminAuthorsController {

    private final AuthorService authorService;

    @Autowired
    public AdminAuthorsController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ModelAttribute("authorDto")
    public Author authorDto() {
        return new Author();
    }

    @ModelAttribute("authorList")
    public List<Author> getAuthors() {
        return authorService.getAllAuthors();
    }

    @ModelAttribute("author")
    public Author getAuthor(@PathVariable(value = "slug", required = false) String authorSlug) {
        return StringUtils.isEmpty(authorSlug) ? null : authorService.getAuthorBySlug(authorSlug);
    }

    @GetMapping("")
    public String getAuthorsPage() {
        return "/admin/authors";
    }

    @GetMapping("/{slug}")
    public String getAuthorProfilePage(@ModelAttribute("author") Author author) {
        return "/admin/author-profile";
    }

    @PostMapping("/add")
    public String addAuthor(@ModelAttribute("authorDto") Author author) {
        authorService.saveAuthor(author);
        return "redirect:/admin/authors";
    }

    @PostMapping("/edit")
    public String editAuthor(@RequestParam("slug") String authorSlug, @RequestParam("name") String name, @RequestParam("description") String description) {
        if (!StringUtils.isEmpty(authorSlug)) {
            Author author = new Author();
            author.setSlug(authorSlug);
            author.setName(name);
            author.setDescription(description);
            authorService.saveAuthor(author);
        }
        return "redirect:/admin/authors/" + authorSlug;
    }

}