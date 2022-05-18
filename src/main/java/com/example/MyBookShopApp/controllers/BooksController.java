package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.BookReviewDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Rate;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookReviewService bookReviewService;
    private final BookRateService bookRateService;
    private final BookReviewLikeService bookReviewLikeService;
    private final ResourceStorage storage;
    private final UserViewedBooksService userViewedBooksService;

    @Autowired
    public BooksController(BookService bookService, AuthorService authorService, BookRateService bookRateService, BookReviewService bookReviewService,
                           BookReviewLikeService bookReviewLikeService, ResourceStorage storage, UserViewedBooksService userViewedBooksService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookRateService = bookRateService;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeService = bookReviewLikeService;
        this.storage = storage;
        this.userViewedBooksService = userViewedBooksService;
    }

    @ModelAttribute("popularBooks")
    public List<Book> getPopularBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        return bookService.getPageOfPopularBooks(offset, limit).getContent();
    }

    @ModelAttribute("viewBooks")
    public List<Book> getViewedBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        return userViewedBooksService.getPageOfViewedBooks(offset, limit).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<Book> getRecentBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        return bookService.getPageOfNewBooks(offset, limit, null, null).getContent();
    }

    @ModelAttribute("authorBooks")
    public List<Book> getAuthorBooks(@PathVariable(value = "slug", required = false) String authorSlug,
                                     @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit) {
        return authorSlug == null ? null : bookService.getPageOfAuthorBooks(authorSlug, offset, limit).getContent();
    }

    @ModelAttribute("author")
    public Author getAuthor(@PathVariable(value = "slug", required = false) String authorSlug) {
        return authorSlug == null ? null : authorService.getAuthorBySlug(authorSlug);
    }

    @ModelAttribute("book")
    public Book getBook(@PathVariable(value = "slug", required = false) String bookSlug) {
        return bookSlug == null ? null : bookService.getBookBySlug(bookSlug);
    }

    @ModelAttribute("bookReviewData")
    public List<BookReviewDto> getBookReviewData(@PathVariable(value = "slug", required = false) String bookSlug) {
        if (bookSlug == null)
            return null;
        List<BookReviewDto> reviewDtoList = new ArrayList<>();
        List<BookReview> reviewList = bookReviewService.getBookReview(bookSlug);
        for (BookReview review : reviewList) {
            BookReviewDto reviewDto = new BookReviewDto();
            reviewDto.setReview(review);
            reviewDto.setRate(bookRateService.getUserBookRate(review.getBook().getId(), review.getUser().getId()));
            reviewDto.setLikedCount(bookReviewLikeService.getCountReviewByValue(review.getId(), 1));
            reviewDto.setDislikedCount(bookReviewLikeService.getCountReviewByValue(review.getId(), -1));
            reviewDtoList.add(reviewDto);
        }
        Collections.sort(reviewDtoList, (r1, r2) -> (r2.getLikedCount() - r2.getDislikedCount()) - (r1.getLikedCount() - r1.getDislikedCount()));
        return reviewDtoList;
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
        if (bookSlug == null)
            return null;
        Rate[] rates = Rate.values();
        for (int i = 0; i < rates.length; i++) {
            rates[i].setCount(bookRateService.getBookRateValueIs(bookSlug, rates[i].getValue()).size());
        }
        return rates;
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
                                             @RequestParam("offset") Integer offset,
                                             @RequestParam("limit")  Integer limit) {
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
    public String getAuthorBooks(@ModelAttribute("authorBooks") List<Book> authorBooks,
                                 @ModelAttribute("author") Author author) {
        return "/books/author";
    }

    @GetMapping(value = "/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getBookPage(@ModelAttribute("book") Book book, @ModelAttribute("bookReviewData") List<BookReviewDto> bookReviewData,
                              @ModelAttribute("bookRates") Rate[] rates, @ModelAttribute("bookRateCount") Integer bookRateCount,
                              @ModelAttribute("bookRate") Integer bookRate, @PathVariable("slug") String bookSlug) {
        userViewedBooksService.saveBookView(book);
        return "/books/slugmy";
    }

    @PostMapping("/{slug}/img/save")
    public String saveBookImage(@RequestParam("file")MultipartFile file, @PathVariable("slug") String bookSlug) throws IOException {
        String savePath = storage.saveBookImage(file, bookSlug);
        Book bookToUpdate = bookService.getBookBySlug(bookSlug);
        bookToUpdate.setImage(savePath);
        bookService.save(bookToUpdate); // save changes
        return "redirect:/books/" + bookSlug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> getBookFile(@PathVariable("hash") String hash) throws IOException {
        byte[] data = storage.getBookFileByteArray(hash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + storage.getBookFilePath(hash).getFileName().toString())
                .contentType(storage.getBookFileMime(hash))
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping(value = "/rateBook")
    @ResponseBody
    public ResultResponse handlePostponedBookRate(@RequestBody BookReviewData bookRateData) {
        return bookRateService.saveRateBook(bookRateData);
    }

    @PostMapping(value = "/bookReview")
    @ResponseBody
    public ResultResponse handlePostponedBookReview(@RequestBody BookReviewData reviewData) {
        return bookReviewService.saveBookReview(reviewData);
    }

    @PostMapping(value = "/rateBookReview")
    @ResponseBody
    public ResultResponse handlePostponedBookReviewLike(@RequestBody BookReviewData reviewLikeData) {
        return bookReviewLikeService.saveBookReviewLike(reviewLikeData);
    }

}