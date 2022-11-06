package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.dto.AuthorDto;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/admin/author")
public class AdminAuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @Autowired
    public AdminAuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/{slug}")
    public String getAuthorProfilePage(@PathVariable(value = "slug") String authorSlug, Model model,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer activePage) {
        if (!StringUtils.isEmpty(authorSlug)) {
            AuthorDto authorDto = authorService.getAuthorDto(authorSlug);
            model.addAttribute("authorDto", authorDto);
            model.addAttribute("activePage", activePage);
            model.addAttribute("countPage", (int) Math.ceil(authorDto.getCountBooks() / (bookService.getRefreshOffset() * 1.0)));
            model.addAttribute("authorBooks", bookService.getPageOfAuthorBooks(authorSlug, (activePage - 1) * bookService.getRefreshLimit(), bookService.getRefreshLimit()).getContent());
        }
        return "/admin/author-profile";
    }

    @PostMapping("/edit")
    public String editAuthor(AuthorDto authorDto) {
        authorService.saveAuthor(authorDto);
        return "redirect:/admin/author/" + authorDto.getSlug();
    }

}