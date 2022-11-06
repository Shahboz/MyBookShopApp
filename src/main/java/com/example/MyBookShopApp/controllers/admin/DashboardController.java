package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.dto.BookInfoDto;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.GenreService;
import com.example.MyBookShopApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("")
    public String getDashboardPage(Model model) {
        model.addAttribute("booksCount", bookService.getCountBooks());
        model.addAttribute("authorsCount", authorService.getCountAuthors());
        model.addAttribute("usersCount", userService.getCountRegisteredUsers());
        model.addAttribute("genresCount", genreService.getCountGenres());
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

    @GetMapping("/books")
    public String getBooksPage(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") Integer activePage) {
        model.addAttribute("activePage", activePage);
        model.addAttribute("bookInfoDto", new BookInfoDto());
        model.addAttribute("countPage", (int) Math.ceil(bookService.getCountBooks() / (bookService.getRefreshOffset() * 1.0)));
        model.addAttribute("booksList", bookService.getPageOfPopularBooks((activePage - 1) * bookService.getRefreshLimit(), bookService.getRefreshLimit()).getContent());
        return "/admin/books";
    }

    @GetMapping("/authors")
    public String getAuthorsPage(Model model) {
        model.addAttribute("authorList", authorService.getAllAuthorsDto());
        return "/admin/authors";
    }

    @GetMapping("/users")
    public String getUsersPage(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") Integer activePage) {
        model.addAttribute("activePage", activePage);
        model.addAttribute("countPage", (int) Math.ceil(userService.getCountUsers() / (userService.getRefreshOffset() * 1.0)));
        model.addAttribute("usersList", userService.getPageOfUsers((activePage - 1) * userService.getRefreshOffset(), userService.getRefreshLimit()));
        return "/admin/users";
    }

}