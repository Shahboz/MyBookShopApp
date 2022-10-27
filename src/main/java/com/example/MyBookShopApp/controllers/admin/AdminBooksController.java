package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.dto.AuthorBookDto;
import com.example.MyBookShopApp.dto.AuthorDto;
import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.service.AdminBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/admin/books")
public class AdminBooksController {

    private final AdminBookService adminBookService;

    @Autowired
    public AdminBooksController(AdminBookService adminBookService) {
        this.adminBookService = adminBookService;
    }

    @ModelAttribute("authorBookDto")
    public AuthorBookDto authorBookDto() {
        return new AuthorBookDto();
    }

    @ModelAttribute("bookInfoDto")
    public BookInfoDto getBookInfoDto(@PathVariable(value = "slug", required = false) String bookSlug) throws IOException {
        return adminBookService.getBookInfoDto(bookSlug);
    }

    @ModelAttribute("authorList")
    public List<AuthorDto> getAuthors(@PathVariable(value = "slug", required = false) String bookSlug) {
        return adminBookService.getAuthors(bookSlug);
    }

    @ModelAttribute("activePage")
    public Integer getActivePage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer activePage) {
        return activePage;
    }

    @ModelAttribute("countPage")
    public Integer getCountPage() {
        return adminBookService.getCountPage();
    }

    @ModelAttribute("booksList")
    public List<BookInfoDto> getBooksList(@RequestParam(required = false, defaultValue = "1") Integer page) {
        return adminBookService.getPageOfBooks(page);
    }

    @GetMapping(value = {"", "/{slug}"})
    public String getBookPage(@PathVariable(value = "slug", required = false) String slug) {
        return StringUtils.isEmpty(slug) ? "/admin/books" : "/admin/book-profile";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("bookInfoDto") BookInfoDto bookInfoDto) throws ParseException {
        adminBookService.saveBook(bookInfoDto);
        return "redirect:/admin/books";
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute("bookInfoDto") BookInfoDto bookInfoDto) throws ParseException {
        adminBookService.saveBook(bookInfoDto);
        return "redirect:/admin/books/" + (bookInfoDto != null ? bookInfoDto.getSlug() : "");
    }

    @PostMapping("/delete")
    public String deleteBook(String bookSlug) {
        ResultResponse result = adminBookService.deleteBook(bookSlug);
        System.out.println("result = " + (result.getResult() ? "Ok" : result.getError()));
        return "redirect:/admin/books";
    }

    @PostMapping("/{slug}/img/save")
    public String saveBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String bookSlug) throws IOException {
        adminBookService.saveBookImage(file, bookSlug);
        return "redirect:/admin/books/" + bookSlug;
    }

    @GetMapping("/review/delete")
    public String deleteBookReview(@RequestParam(value = "id") Integer reviewId, @RequestParam(value = "book") String bookSlug) {
        adminBookService.deleteReview(reviewId);
        return "redirect:/admin/books/" + bookSlug;
    }

    @PostMapping("/author/add")
    public String addAuthorBook(AuthorBookDto authorBookDto) {
        adminBookService.addAuthorBook(authorBookDto);
        return "redirect:/admin/books/" + authorBookDto.getBookSlug();
    }

    @GetMapping("/author/delete")
    public String deleteAuthorBook(@RequestParam(value = "book") String bookSlug, @RequestParam(value = "id") Integer authorBookId) {
        adminBookService.deleteAuthorBook(authorBookId);
        return "redirect:/admin/books/" + bookSlug;
    }

}