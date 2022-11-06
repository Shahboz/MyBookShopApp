package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.BookReviewDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Rate;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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
        return authorSlug == null ? null : bookService.getPageOfAuthorBooks(authorSlug, 0, bookService.getRefreshLimit()).getContent();
    }

    @ModelAttribute("author")
    public Author getAuthor(@PathVariable(value = "slug", required = false) String authorSlug) {
        return authorSlug == null ? null : authorService.getAuthorBySlug(authorSlug);
    }

    @ModelAttribute("book")
    public Book getBook(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookSlug == null ? null : bookService.getBookBySlug(bookSlug);
    }

    @ModelAttribute("isLimitDownloadExceeded")
    public Boolean checkLimitDownload(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookService.limitDownloadExceded(bookSlug);
    }

    @ModelAttribute("bookFilesData")
    public List<BookFileDto> getBookFilesData(@PathVariable(value = "slug", required = false) String bookSlug) throws IOException {
        return bookSlug == null ? null : bookService.getBookFilesData(bookSlug);
    }

    @ModelAttribute("bookReviewData")
    public List<BookReviewDto> getBookReviewData(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookReviewService.getBookReviewDtoList(bookSlug, 0, bookReviewService.getBookReviewCount(bookSlug));
    }

    @ModelAttribute("bookRate")
    public Integer calcBookRate(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookSlug == null ? 0 :  bookRateService.calcBookRate(bookSlug);
    }

    @ModelAttribute("bookRateCount")
    public Integer getBookRateCount(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookSlug == null ? 0 : bookRateService.getBookRateCount(bookSlug);
    }

    @ModelAttribute("bookRates")
    public Rate[] getBookRates(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookRateService.getBookRates(bookSlug);
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
    public String getAuthorBooks(@ModelAttribute("authorBooks") List<Book> authorBooks, @ModelAttribute("author") Author author) {
        return "/books/author";
    }

    @GetMapping(value = "/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getBookPage(@ModelAttribute("book") Book book, @ModelAttribute("bookReviewData") List<BookReviewDto> bookReviewData,
                              @ModelAttribute("bookRates") Rate[] rates, @ModelAttribute("bookRateCount") Integer bookRateCount,
                              @ModelAttribute("bookRate") Integer bookRate, @ModelAttribute("isLimitDownloadExceeded") Boolean isLimitDownloadExceeded,
                              @ModelAttribute("bookFilesData") List<BookFileDto> bookFileDtoList) {
        userViewedBooksService.saveBookView(book);
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