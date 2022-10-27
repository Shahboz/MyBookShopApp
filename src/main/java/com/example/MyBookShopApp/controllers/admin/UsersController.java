package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.dto.UserDto;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.service.BookReviewService;
import com.example.MyBookShopApp.service.UserBooksService;
import com.example.MyBookShopApp.service.UserContactService;
import com.example.MyBookShopApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/admin/users")
public class UsersController {

    private final UserService userService;
    private final BookstoreUserRegister userRegister;
    private final UserBooksService userBooksService;
    private final UserContactService userContactService;
    private final BookReviewService bookReviewService;

    @Autowired
    public UsersController(UserService userService, BookstoreUserRegister userRegister, UserBooksService userBooksService,
                           UserContactService userContactService, BookReviewService bookReviewService) {
        this.userService = userService;
        this.userRegister = userRegister;
        this.userBooksService = userBooksService;
        this.userContactService = userContactService;
        this.bookReviewService = bookReviewService;
    }

    private UserDto setUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUser(user);
        userDto.setPaidBooksList(userBooksService.getUserBooks(user, "PAID"));
        userDto.setArchivedBooksList(userBooksService.getUserBooks(user, "ARCHIVED"));
        userDto.setBookReviewList(bookReviewService.getUserBookReviewData(user.getHash()));
        for (UserContact contact : userContactService.getApprovedUserContacts(user.getId())) {
            if (contact.getType().equals("EMAIL")) {
                userDto.setContactEmail(contact.getContact());
            } else if (contact.getType().equals("PHONE")) {
                userDto.setContactPhone(contact.getContact());
            }
        }
        return userDto;
    }

    @ModelAttribute("userDto")
    public UserDto getUserDto(@PathVariable(value = "userHash", required = false) String userHash) {
        User user = StringUtils.isEmpty(userHash) ? null : userService.getUserByHash(userHash);
        return user == null ? null : setUserDto(user);
    }

    @ModelAttribute("usersList")
    public List<UserDto> getUsersDto() {
        List usersList = new ArrayList();
        for (User user : userService.getRegisterUsers()) {
            usersList.add(setUserDto(user));
        }
        return usersList;
    }

    @GetMapping("")
    public String getUsersPage() {
        return "/admin/users";
    }

    @GetMapping("/{userHash}")
    public String getUserProfilePage(@ModelAttribute("userDto") UserDto userDto) {
        return "/admin/user-profile";
    }

    @PostMapping("/{hash}/edit")
    public String updatePassword(@PathVariable(value = "hash") String hash, @RequestParam String password, @RequestParam String confirm) {
        User user = userService.getUserByHash(hash);
        if (user != null && !StringUtils.isEmpty(password) && !StringUtils.isEmpty(confirm) && password.equals(confirm)) {
            user.setPassword(userRegister.encodePassword(password));
            userService.save(user);
        }
        return "redirect:/admin/users/" + hash;
    }

}