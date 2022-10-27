package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.GenreService;
import com.example.MyBookShopApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/admin")
public class DashboardController {

    private final BookService bookService;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final UserService userService;

    @Autowired
    public DashboardController(BookService bookService, GenreService genreService, AuthorService authorService, UserService userService) {
        this.bookService = bookService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.userService = userService;
    }

    @ModelAttribute("booksCount")
    public Integer getCountBooks() {
        return bookService.getCountBooks();
    }

    @ModelAttribute("authorsCount")
    public Integer getCountAuthors() {
        return authorService.getAllAuthors().size();
    }

    @ModelAttribute("usersCount")
    public Integer getCountRegisterUsers() {
        return userService.getRegisterUsers().size();
    }

    @ModelAttribute("genresCount")
    public Integer getCountGenres() {
        return genreService.getCountGenres();
    }

    @GetMapping("")
    public String getDashboardPage() {
        return "/admin/dashboard";
    }

    @GetMapping(value = "/profile")
    public String getProfilePage() {
        return "/admin/profile";
    }

    @GetMapping("/billing")
    public String getBillingPage() {
        return "/admin/billing";
    }

    @GetMapping("/notifications")
    public String getNotificationsPage() {
        return "/admin/notifications";
    }

    @GetMapping("/virtual-reality")
    public String getVirtualRealityPage() {
        return "/admin/virtual-reality";
    }

    @GetMapping("/template")
    public String getTemplatePage() {
        return "/admin/template";
    }

}