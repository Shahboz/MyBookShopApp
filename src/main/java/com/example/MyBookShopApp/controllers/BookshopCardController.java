package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import com.example.MyBookShopApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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

    @GetMapping("/cart")
    public String getCartPage(@CookieValue(value = "cartContents", required = false) String cartContents, Model model) {
        Integer sumCartDiscount, sumCart;
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser != null) {
            List<Book> bookList = userBooksService.getUserBooksByUserBookType(currentUser.getId(), "CART");
            sumCartDiscount = bookList.stream().mapToInt(Book::getDiscountPrice).sum();
            sumCart = bookList.stream().mapToInt(Book::getPrice).sum();
            model.addAttribute("bookCart", bookList);
        }
        else if (cartContents== null || cartContents.equals("")) {
            sumCart = 0;
            sumCartDiscount = 0;
        } else {
            List<Book> booksFromCookiesSlug = bookService.getBooksBySlugs(getSlugFromString(cartContents));
            sumCartDiscount = booksFromCookiesSlug.stream().mapToInt(Book::getDiscountPrice).sum();
            sumCart = booksFromCookiesSlug.stream().mapToInt(Book::getPrice).sum();
            model.addAttribute("bookCart", booksFromCookiesSlug);
        }
        model.addAttribute("sumCartDiscount", sumCartDiscount);
        model.addAttribute("sumCart", sumCart);
        return "cart";
    }

    @GetMapping("/postponed")
    public String getPostponedPage(@CookieValue(value = "postponedContents", required = false) String postponedContents, Model model) {
        if (postponedContents != null && !postponedContents.equals("")) {
            List<Book> booksFromCookiesSlug = bookService.getBooksBySlugs(getSlugFromString(postponedContents));
            model.addAttribute("bookPostponed", booksFromCookiesSlug);
        }
        return "postponed";
    }

    @PostMapping("/changeBookStatus/{slug}")
    @ResponseBody
    public ResultResponse handleChangeBookStatus(@RequestBody BookStatus bookStatus,
                                                 @CookieValue(name = "cartContents", required = false) String cartContents,
                                                 @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                 HttpServletResponse response) {
        ResultResponse resultResponse = new ResultResponse();
        User currentUser = (User) userRegister.getCurrentUser();
        Book book = bookService.getBookBySlug(bookStatus.getBooksIds());
        UserBookType bookType = userBooksService.getUserBookType(bookStatus.getStatus());

        if (book == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Книга не найдена");
        } else if (bookStatus.getStatus().equals("UNLINK")) {
            // Удаление только для авторизованных пользователей
            if (currentUser != null) {
                UserBooks userCartBook = userBooksService.getUserBookByType(currentUser.getId(), book.getId(), "CART");
                UserBooks userKeptBook = userBooksService.getUserBookByType(currentUser.getId(), book.getId(), "KEPT");
                if (userCartBook != null) {
                    userBooksService.delete(userCartBook);
                } else if (userKeptBook != null) {
                    userBooksService.delete(userKeptBook);
                }
            }

            if (postponedContents != null && !postponedContents.equals("") && postponedContents.contains(book.getSlug())) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                cookieBooks.remove(book.getSlug());
                Cookie cookie = new Cookie("postponedContents", String.join("/", cookieBooks));
                cookie.setPath("/books");
                response.addCookie(cookie);
            } else if (cartContents != null && !cartContents.equals("") && cartContents.contains(book.getSlug())) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                cookieBooks.remove(book.getSlug());
                Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
                cookie.setPath("/books");
                response.addCookie(cookie);
            }

            resultResponse.setResult(true);
        }
        else if (bookType == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Некорректный тип привязки");
        } else {
            // Сохранение только для авторизованных пользователей
            if (currentUser != null) {
                UserBooks userBook;
                UserBooks userCartBook = userBooksService.getUserBookByType(currentUser.getId(), book.getId(), "CART");
                UserBooks userKeptBook = userBooksService.getUserBookByType(currentUser.getId(), book.getId(), "KEPT");
                UserBooks userPaidBook = userBooksService.getUserBookByType(currentUser.getId(), book.getId(), "PAID");

                if (userPaidBook == null) {
                    if (userCartBook != null) {
                        userBook = userCartBook;
                    } else if (userKeptBook != null) {
                        userBook = userKeptBook;
                    } else {
                        userBook = new UserBooks();
                        userBook.setUser(currentUser);
                        userBook.setBook(book);
                    }

                    userBook.setType(bookType);
                    userBook.setTime(new Date());
                    userBooksService.save(userBook);

                    resultResponse.setResult(true);
                } else {
                    resultResponse.setResult(false);

                    if (bookType.getCode() == "PAID" || bookType.getCode() == "CART")
                        resultResponse.setError("Книга уже куплена");
                    else if (bookType.getCode() == "KEPT")
                        resultResponse.setError("Нельзя отложить купленную книгу");
                }
            }

            switch (bookType.getCode()) {

                case "CART" :
                    if (cartContents == null || cartContents.equals("")) {
                        Cookie cookie = new Cookie("cartContents", book.getSlug());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    } else if (!cartContents.contains(book.getSlug())) {
                        StringJoiner stringJoiner = new StringJoiner("/");
                        stringJoiner.add(cartContents).add(book.getSlug());
                        Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    }
                    // При добавлении в корзину из отложенных удаляем книгу
                    if (postponedContents != null && postponedContents.contains(book.getSlug())) {
                        ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                        cookieBooks.remove(book.getSlug());
                        Cookie cookie = new Cookie("postponedContents", String.join("/", cookieBooks));
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    }
                    break;
                case "KEPT" :
                    if (postponedContents == null || postponedContents.equals("")) {
                        Cookie cookie = new Cookie("postponedContents", book.getSlug());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    } else if (!postponedContents.contains(book.getSlug())) {
                        StringJoiner stringJoiner = new StringJoiner("/");
                        stringJoiner.add(postponedContents).add(book.getSlug());
                        Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    }
                    // При добавлении в отложенное из корзины удаляем книгу
                    if (cartContents != null && cartContents.contains(book.getSlug())) {
                        ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                        cookieBooks.remove(book.getSlug());
                        Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
                        cookie.setPath("/books");
                        response.addCookie(cookie);
                    }
                    break;

                default:
                    resultResponse.setResult(false);
                    resultResponse.setError("Некорректный тип привязки:" + bookStatus.getStatus());
            }
        }

        return resultResponse;
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
            List<Book> booksList = userBooksService.getUserBooksByUserBookType(currentUser.getId(), "CART");
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