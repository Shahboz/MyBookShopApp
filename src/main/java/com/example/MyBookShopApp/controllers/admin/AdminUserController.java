package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.service.BookReviewService;
import com.example.MyBookShopApp.service.UserBooksService;
import com.example.MyBookShopApp.service.UserContactService;
import com.example.MyBookShopApp.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    private final UserService userService;
    private final UserContactService userContactService;
    private final BookstoreUserRegister userRegister;
    private final UserBooksService userBooksService;
    private final BookReviewService bookReviewService;

    @Autowired
    public AdminUserController(UserService userService, UserContactService userContactService, BookstoreUserRegister userRegister,
                               UserBooksService userBooksService, BookReviewService bookReviewService) {
        this.userService = userService;
        this.userContactService = userContactService;
        this.userRegister = userRegister;
        this.userBooksService = userBooksService;
        this.bookReviewService = bookReviewService;
    }

    @GetMapping("/{userHash}")
    public String getUserPage(Model model, @PathVariable(value = "userHash") String userHash) {
        if (!StringUtils.isEmpty(userHash)) {
            model.addAttribute("userDto", userService.getUserDtoByHash(userHash));
            userContactService.getApprovedUserContacts(userHash)
                    .forEach(contact -> model.addAttribute(contact.getType().equals("PHONE") ? "contactPhone" : "contactEmail", contact.getContact()));
            model.addAttribute("userPaidBooks", userBooksService.getUserBooks(userService.getUserByHash(userHash), "PAID"));
            model.addAttribute("userArchivedBooks", userBooksService.getUserBooks(userService.getUserByHash(userHash), "ARCHIVED"));
            model.addAttribute("userBookReviews", bookReviewService.getUserBookReviewDtoList(userHash));
        }
        return "/admin/user-profile";
    }

    @PostMapping("/{hash}/edit")
    public String updatePassword(@PathVariable(value = "hash") String userHash, @RequestParam String password) {
        userRegister.updatePassword(userHash, password);
        return "redirect:/admin/user/" + userHash;
    }

}