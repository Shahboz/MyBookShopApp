package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
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
import java.util.*;


@Controller
@RequestMapping("/books")
public class BookshopCardController {

    private final BookService bookService;
    private final UserBooksService userBooksService;
    private final PaymentService paymentService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public BookshopCardController(BookService bookService, UserBooksService userBooksService, PaymentService paymentService, BookstoreUserRegister userRegister) {
        this.bookService = bookService;
        this.userBooksService = userBooksService;
        this.paymentService = paymentService;
        this.userRegister = userRegister;
    }

    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    @ModelAttribute(name = "errorPay")
    public String errorPay() {
        return null;
    }

    private String[] getSlugFromString(String contents) {
        contents = contents.startsWith("/") ? contents.substring(1) : contents;
        contents = contents.endsWith("/") ? contents.substring(0, contents.length() - 1) : contents;
        return contents.split("/");
    }

    private String addBookToCookie(String bookSlug, String cookieValue) {
        String value = cookieValue;
        if (StringUtils.isEmpty(cookieValue)) {
            value = bookSlug;
        } else if (!cookieValue.contains(bookSlug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cookieValue).add(bookSlug);
            value = stringJoiner.toString();
        }
        return value;
    }

    private String deleteBookFromCookie(String bookSlug, String cookieValue) {
        String value = cookieValue;
        if (!StringUtils.isEmpty(cookieValue) && cookieValue.contains(bookSlug)) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cookieValue.split("/")));
            cookieBooks.remove(bookSlug);
            value = String.join("/", cookieBooks);
        }
        return value;
    }

    @GetMapping("/cart")
    public String getCartPage(@CookieValue(value = "cartContents", required = false) String cartContents, Model model) {
        Integer sumCartDiscount = 0, sumCart = 0;
        List<Book> bookCart = null;
        if (userRegister.getCurrentUser() != null) {
            bookCart = userBooksService.getUserCartBooks();
            sumCartDiscount = bookCart.stream().mapToInt(Book::getDiscountPrice).sum();
            sumCart = bookCart.stream().mapToInt(Book::getPrice).sum();
        } else if (!StringUtils.isEmpty(cartContents)) {
            bookCart = bookService.getBooksBySlugs(getSlugFromString(cartContents));
            sumCartDiscount = bookCart.stream().mapToInt(Book::getDiscountPrice).sum();
            sumCart = bookCart.stream().mapToInt(Book::getPrice).sum();
        }
        model.addAttribute("sumCart", sumCart);
        model.addAttribute("sumCartDiscount", sumCartDiscount);
        model.addAttribute("bookCart", bookCart);
        return "cart";
    }

    @GetMapping("/postponed")
    public String getPostponedPage(@CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {
        List<Book> bookPostponed = null;
        if (userRegister.getCurrentUser() != null) {
            bookPostponed = userBooksService.getUserKeptBooks();
        } else if (!StringUtils.isEmpty(postponedContents)) {
            bookPostponed = bookService.getBooksBySlugs(getSlugFromString(postponedContents));
        }
        model.addAttribute("bookPostponed", bookPostponed);
        return "postponed";
    }

    @PostMapping("/changeBookStatus")
    @ResponseBody
    public ResultResponse handleChangeBookStatus(@RequestBody BookStatus bookStatus,
                                                 @CookieValue(name = "cartContents", required = false) String cartContents,
                                                 @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                 HttpServletResponse response) {
        ResultResponse resultResponse = null;

        if (bookStatus != null && !StringUtils.isEmpty(bookStatus.getStatus()) && !StringUtils.isEmpty(bookStatus.getBooksIds())) {
            // Сохранение в БД
            for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                Book book = bookService.getBookBySlug(bookSlug);
                resultResponse = userBooksService.changeBookStatus(bookStatus.getStatus(), book);
                if (!resultResponse.getResult())
                    break;
            }

            // Обработка куки
            String cookieValue = null;
            Cookie cookieCart = new Cookie("cartContents", null);
            Cookie cookieKept = new Cookie("postponedContents", null);
            cookieCart.setPath("/books");
            cookieKept.setPath("/books");
            switch (bookStatus.getStatus().toUpperCase()) {
                case "UNLINK" :
                    if (!StringUtils.isEmpty(postponedContents)) {
                        cookieValue = postponedContents;
                        for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                            cookieValue = deleteBookFromCookie(bookSlug, cookieValue);
                        }
                        cookieKept.setValue(cookieValue);
                        response.addCookie(cookieKept);
                    }
                    if (!StringUtils.isEmpty(cartContents)) {
                        cookieValue = cartContents;
                        for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                            cookieValue = deleteBookFromCookie(bookSlug, cookieValue);
                        }
                        cookieCart.setValue(cookieValue);
                        response.addCookie(cookieCart);
                    }
                    break;
                case "CART" :
                    cookieValue = cartContents;
                    for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                        cookieValue = addBookToCookie(bookSlug, cookieValue);
                    }
                    cookieCart.setValue(cookieValue);
                    response.addCookie(cookieCart);
                    // При добавлении в корзину из отложенных удаляем книгу
                    if (!StringUtils.isEmpty(postponedContents)) {
                        cookieValue = postponedContents;
                        for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                            cookieValue = deleteBookFromCookie(bookSlug, cookieValue);
                        }
                        cookieKept.setValue(cookieValue);
                        response.addCookie(cookieKept);
                    }
                    break;
                case "KEPT" :
                    cookieValue = postponedContents;
                    for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                        cookieValue = addBookToCookie(bookSlug, cookieValue);
                    }
                    cookieKept.setValue(cookieValue);
                    response.addCookie(cookieKept);
                    // При добавлении в отложенное из корзины удаляем книгу
                    if (!StringUtils.isEmpty(cartContents)) {
                        cookieValue = cartContents;
                        for (String bookSlug : bookStatus.getBooksIds().split(",")) {
                            cookieValue = deleteBookFromCookie(bookSlug, cookieValue);
                        }
                        cookieCart.setValue(cookieValue);
                        response.addCookie(cookieCart);
                    }
                    break;
            }
        }

        return resultResponse;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> getBookFile(@PathVariable("hash") String fileHash) throws IOException {
        Boolean isPaid = userBooksService.getUserPaidBooks().stream().flatMap(book -> book.getBookFileList().stream()).anyMatch(bookFile -> bookFile.equals(fileHash));
        if (!StringUtils.isEmpty(fileHash) && isPaid) {
            return bookService.downloadBookFile(fileHash);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/payment")
    public RedirectView handlePayment() throws NoSuchAlgorithmException {
        List<Book> booksFromCookiesSlug = bookService.getBooksBySlugs(getSlugFromString(null));
        String paymentUrl = paymentService.getPaymentUrl(booksFromCookiesSlug);
        return new RedirectView(paymentUrl);
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