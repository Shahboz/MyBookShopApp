package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.*;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookReviewService bookReviewService;
    private final BookRateService bookRateService;
    private final UserViewedBooksService userViewedBooksService;

    @Autowired
    public BooksController(BookService bookService, AuthorService authorService, BookRateService bookRateService,
                           BookReviewService bookReviewService, UserViewedBooksService userViewedBooksService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookRateService = bookRateService;
        this.bookReviewService = bookReviewService;
        this.userViewedBooksService = userViewedBooksService;
    }

    @ModelAttribute("popularBooks")
    public List<Book> getPopularBooks() {
        return bookService.getPageOfPopularBooks(0, bookService.getRefreshLimit()).getContent();
    }

    @ModelAttribute("viewBooks")
    public List<Book> getViewedBooks() {
        return userViewedBooksService.getPageOfViewedBooks(0, bookService.getRefreshLimit()).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<Book> getRecentBooks() {
        return bookService.getPageOfNewBooks(0, bookService.getRefreshLimit(), null, null).getContent();
    }

    @ModelAttribute("authorBooks")
    public List<Book> getAuthorBooks(@PathVariable(value = "slug", required = false) String authorSlug) {
        return bookService.getPageOfAuthorBooks(authorSlug, 0, bookService.getRefreshLimit()).getContent();
    }

    @ModelAttribute("author")
    public AuthorDto getAuthor(@PathVariable(value = "slug", required = false) String authorSlug) {
        return authorService.getAuthorDto(authorSlug);
    }

    @GetMapping(value = "/recommended", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getRecommendedBooksByPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }

    @GetMapping(value = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getRecentBooksByPage(@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                             @RequestParam(value = "to",   required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate,
                                             @RequestParam("offset") Integer offset, @RequestParam("limit")  Integer limit) {
        return new BooksPageDto(bookService.getPageOfNewBooks(offset, limit, fromDate, toDate).getContent());
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getPopularBooksByPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }

    @GetMapping(value = "/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getViewBooksByPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(userViewedBooksService.getPageOfViewedBooks(offset, limit).getContent());
    }

    @GetMapping(value = "/author/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getAuthorBooksByPage(@PathVariable("slug") String authorSlug, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfAuthorBooks(authorSlug, offset, limit).getContent());
    }

    @GetMapping(value = "/genre/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getGenreBooksByPage(@PathVariable("slug") String genreSlug, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfGenreBooks(genreSlug, offset, limit).getContent());
    }

    @GetMapping(value = "/tag/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getTagBooksByPage(@PathVariable("slug") String tagSlug, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfTagBooks(tagSlug, offset, limit).getContent());
    }

    @GetMapping(value = "/recent", produces = MediaType.TEXT_HTML_VALUE)
    public String getRecentPage(@ModelAttribute("recentBooks") List<Book> recentBooks) {
        return "/books/recent";
    }

    @GetMapping(value = "/popular", produces = MediaType.TEXT_HTML_VALUE)
    public String getPopularPage(@ModelAttribute("popularBooks") List<Book> popularBooks) {
        return "/books/popular";
    }

    @GetMapping(value = "/view", produces = MediaType.TEXT_HTML_VALUE)
    public String getViewedPage(@ModelAttribute("viewBooks") List<Book> viewedBooks) {
        return "/books/view";
    }

    @GetMapping(value = "/author/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getAuthorBooks(@ModelAttribute("authorBooks") List<Book> authorBooks, @ModelAttribute("author") AuthorDto author) {
        return "/books/author";
    }

    @GetMapping(value = "/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getBookPage(@PathVariable(value = "slug") String bookSlug, Model model) throws IOException {
        model.addAttribute("book", bookService.getBookBySlug(bookSlug));
        model.addAttribute("bookRates", bookRateService.getBookRates(bookSlug));
        model.addAttribute("bookRateCount", bookRateService.getBookRateCount(bookSlug));
        model.addAttribute("bookRate", bookRateService.calcBookRate(bookSlug));
        model.addAttribute("isLimitDownloadExceeded", bookService.limitDownloadExceded(bookSlug));
        model.addAttribute("bookFilesData", bookService.getBookFilesData(bookSlug));
        model.addAttribute("bookReviewData", bookReviewService.getBookReviewDtoList(bookSlug, 0, bookReviewService.getBookReviewCount(bookSlug)));
        userViewedBooksService.saveBookView(bookService.getBookBySlug(bookSlug));
        return "/books/slugmy";
    }

    @PostMapping(value = "/rateBook")
    @ResponseBody
    public ResultResponse handlePostponedBookRate(@RequestBody BookReviewData bookRateData) {
        BookReview bookReview = bookReviewService.getUserBookReview(bookRateData.getBookId());
        return bookRateService.saveRateBook(bookService.getBookById(bookRateData.getBookId()), bookReview, bookRateData.getValue());
    }

    @PostMapping(value = "/bookReview")
    @ResponseBody
    public ResultResponse handlePostponedBookReview(@RequestBody BookReviewData reviewData) {
        return bookReviewService.saveBookReview(bookService.getBookById(reviewData.getBookId()), reviewData.getText());
    }

    @PostMapping(value = "/rateBookReview")
    @ResponseBody
    public ResultResponse handlePostponedBookReviewLike(@RequestBody BookReviewData reviewLikeData) {
        return bookReviewService.bookReviewLiked(reviewLikeData);
    }

}