package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.security.RegistrationForm;
import com.example.MyBookShopApp.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


@Controller
@RequestMapping("/books")
public class BookshopCardController {

    private final BookService bookService;
    private final UserBooksService userBooksService;
    private final PaymentService paymentService;
    private final BookstoreUserRegister userRegister;
    private final UserService userService;

    @Autowired
    public BookshopCardController(BookService bookService, UserBooksService userBooksService, PaymentService paymentService,
                                  BookstoreUserRegister userRegister, UserService userService) {
        this.bookService = bookService;
        this.userBooksService = userBooksService;
        this.paymentService = paymentService;
        this.userRegister = userRegister;
        this.userService = userService;
    }

    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    @ModelAttribute(name = "errorPay")
    public String errorPay() {
        return null;
    }

    @GetMapping("/cart")
    public String getCartPage(@CookieValue(value = "anonymousUser", required = false) String userHash, Model model) {
        User currentUser = userRegister.getCurrentUser() != null ? (User) userRegister.getCurrentUser() : userService.getUserByHash(userHash);
        List<Book> bookCart = currentUser == null ? new ArrayList<>() : userBooksService.getUserBooks(currentUser, "CART");
        Integer sumCart = bookCart.stream().mapToInt(Book::getPrice).sum();
        Integer sumCartDiscount = bookCart.stream().mapToInt(Book::getDiscountPrice).sum();
        model.addAttribute("sumCart", sumCart);
        model.addAttribute("sumCartDiscount", sumCartDiscount);
        model.addAttribute("bookCart", bookCart);
        return "cart";
    }

    @GetMapping("/postponed")
    public String getPostponedPage(@CookieValue(value = "anonymousUser", required = false) String userHash, Model model) {
        User currentUser = userRegister.getCurrentUser() != null ? (User) userRegister.getCurrentUser() : userService.getUserByHash(userHash);
        List<Book> bookPostponed = currentUser == null ? new ArrayList<>() : userBooksService.getUserBooks(currentUser, "KEPT");
        model.addAttribute("bookPostponed", bookPostponed);
        return "postponed";
    }

    @PostMapping("/changeBookStatus")
    @ResponseBody
    public ResultResponse handleChangeBookStatus(@RequestBody BookStatus bookStatus,
                                                 @CookieValue(name = "anonymousUser", required = false) String userHash,
                                                 HttpServletResponse response) throws NoSuchAlgorithmException {
        ResultResponse resultResponse = null;
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser == null && !StringUtils.isEmpty(userHash)) {
            currentUser = userService.getUserByHash(userHash);
        }
        if (currentUser == null) {
            RegistrationForm registrationForm = new RegistrationForm();
            registrationForm.setName("anonymousUser" + SecureRandom.getInstanceStrong().nextInt(1000));
            currentUser = userRegister.registerNewUser(registrationForm);
            Cookie cookie = new Cookie("anonymousUser", currentUser.getHash());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/books");
            response.addCookie(cookie);
        }

        if (bookStatus != null && !StringUtils.isEmpty(bookStatus.getStatus()) && !StringUtils.isEmpty(bookStatus.getBooksIds())) {
            // Сохранение в БД
            for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                Book book = bookService.getBookBySlug(bookSlug);
                resultResponse = userBooksService.changeBookStatus(bookStatus.getStatus(), book, currentUser);
                if (!resultResponse.getResult())
                    break;
            }
        }

        return resultResponse;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> getBookFile(@PathVariable("hash") String fileHash) throws IOException {
        boolean isPaid = userBooksService.getUserPaidBooks().stream().flatMap(book -> book.getBookFileList().stream()).anyMatch(bookFile -> bookFile.getHash().equals(fileHash));
        if (!StringUtils.isEmpty(fileHash) && isPaid) {
            return bookService.downloadBookFile(fileHash);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/pay")
    public String handlePay(RedirectAttributes redirectAttributes) {
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser != null) {
            List<Book> booksList = userBooksService.getUserCartBooks();
            ResultResponse resultResponse = paymentService.processPayUserBooks(currentUser, booksList);
            if (resultResponse.getResult()) {
                return "redirect:/my";
            } else {
                redirectAttributes.addFlashAttribute("errorPay", resultResponse.getError());
                return "redirect:/books/cart";
            }
        }
        return "redirect:/signin";
    }

}