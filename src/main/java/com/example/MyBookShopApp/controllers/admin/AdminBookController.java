package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.service.AuthorBooksService;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookReviewService;
import com.example.MyBookShopApp.service.BookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.ParseException;


@Controller
@RequestMapping("/admin/book")
public class AdminBookController {

    private final AuthorService authorService;
    private final AuthorBooksService authorBooksService;
    private final BookReviewService bookReviewService;
    private final BookService bookService;

    @Autowired
    public AdminBookController(AuthorService authorService, AuthorBooksService authorBooksService, BookReviewService bookReviewService, BookService bookService) {
        this.authorService = authorService;
        this.authorBooksService = authorBooksService;
        this.bookReviewService = bookReviewService;
        this.bookService = bookService;
    }

    @GetMapping("/{slug}")
    public String getBookProfile(@PathVariable(value = "slug") String bookSlug, Model model,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") Integer activePage) throws IOException {
        if (!StringUtils.isEmpty(bookSlug)) {
            model.addAttribute("activePage", activePage);
            model.addAttribute("countPage", (int) Math.ceil(bookReviewService.getBookReviewCount(bookSlug) / (bookReviewService.getRefreshOffset() * 1.0)));
            model.addAttribute("authorList", authorService.getAllAuthorsDto());
            model.addAttribute("bookInfoDto", bookService.getBookInfoDto(bookSlug));
            model.addAttribute("authorsBook", authorBooksService.getAuthorsBook(bookSlug));
            model.addAttribute("reviewsBook", bookReviewService.getBookReviewDtoList(bookSlug, (activePage - 1) * bookReviewService.getRefreshLimit(), bookReviewService.getRefreshLimit()));
        }
        return "/admin/book-profile";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("bookInfoDto") BookInfoDto bookInfoDto) throws ParseException {
        bookService.saveBook(bookInfoDto);
        return "redirect:/admin/books";
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute("bookInfoDto") BookInfoDto bookInfoDto) throws ParseException {
        bookService.saveBook(bookInfoDto);
        return "redirect:/admin/book/" + bookInfoDto.getSlug();
    }

    @PostMapping("/delete")
    public String deleteBook(String bookSlug) {
        bookService.deleteBook(bookSlug);
        return "redirect:/admin/books";
    }

    @PostMapping("/{slug}/img/save")
    public String saveBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String bookSlug) throws IOException {
        bookService.saveBookImage(file, bookSlug);
        return "redirect:/admin/book/" + bookSlug;
    }

    @PostMapping("/author/add")
    public String addAuthorBook(@RequestParam String bookSlug, @RequestParam String authorSlug) {
        authorBooksService.saveAuthorBook(bookService.getBookBySlug(bookSlug), authorService.getAuthorBySlug(authorSlug));
        return "redirect:/admin/book/" + bookSlug;
    }

    @GetMapping("/author/delete")
    public String deleteAuthorBook(@RequestParam(value = "book") String bookSlug, @RequestParam(value = "id") Integer authorBookId) {
        authorBooksService.deleteAuthorBook(authorBookId);
        return "redirect:/admin/book/" + bookSlug;
    }

}