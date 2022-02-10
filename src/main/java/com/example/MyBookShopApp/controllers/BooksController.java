package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Rate;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.dto.BooksPageDto;
import com.example.MyBookShopApp.service.*;
import org.jboss.logging.Logger;
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
import java.nio.file.Path;
import java.util.*;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookReviewService bookReviewService;
    private final BookRateService bookRateService;
    private final UserService userService;
    private final BookReviewLikeService bookReviewLikeService;
    private final ResourceStorage storage;

    @Autowired
    public BooksController(BookService bookService, AuthorService authorService, BookRateService bookRateService,
                           BookReviewService bookReviewService, UserService userService, BookReviewLikeService bookReviewLikeService,
                           ResourceStorage storage) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookRateService = bookRateService;
        this.bookReviewService = bookReviewService;
        this.userService = userService;
        this.bookReviewLikeService = bookReviewLikeService;
        this.storage = storage;
    }

    @ModelAttribute("popularBooks")
    public List<Book> getPopularBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit",  required = false, defaultValue = "6") Integer limit){
        return bookService.getPageOfPopularBooks(offset, limit).getContent();
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
        if (authorSlug == null)
            return null;
        return bookService.getPageOfAuthorBooks(authorSlug, offset, limit).getContent();
    }

    @ModelAttribute("author")
    public Author getAuthor(@PathVariable(value = "slug", required = false) String authorSlug) {
        if (authorSlug == null)
            return null;
        return authorService.getAuthorBySlug(authorSlug);
    }

    @ModelAttribute("book")
    public Book getBook(@PathVariable(value = "slug", required = false) String bookSlug) {
        if(bookSlug == null)
            return null;
        return bookService.getBookBySlug(bookSlug);
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
        if (bookSlug == null)
            return 0;
        return bookRateService.calcBookRate(bookSlug);
    }

    @ModelAttribute("bookRateCount")
    public Integer getBookRateCount(@PathVariable(value = "slug", required = false) String bookSlug) {
        if (bookSlug == null)
            return 0;
        return bookRateService.getBookRateCount(bookSlug);
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
                                             @RequestParam("limit")  Integer limit
    ) {
        return new BooksPageDto(bookService.getPageOfNewBooks(offset, limit, fromDate, toDate).getContent());
    }

    @GetMapping(value = "/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BooksPageDto getPopularBooksByPage(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
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

    @GetMapping(value = "/author/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getAuthorBooks(@ModelAttribute("authorBooks") List<Book> authorBooks,
                                 @ModelAttribute("author") Author author) {
        return "/books/author";
    }

    @GetMapping(value = "/{slug}", produces = MediaType.TEXT_HTML_VALUE)
    public String getBookPage(@ModelAttribute("book") Book book, @ModelAttribute("bookReviewData") List<BookReviewDto> bookReviewData,
                              @ModelAttribute("bookRates") Rate[] rates, @ModelAttribute("bookRateCount") Integer bookRateCount,
                              @ModelAttribute("bookRate") Integer bookRate) {
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
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file path: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("book file data length: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping(value = "/rateBook")
    @ResponseBody
    public ResultResponse handlePostponedBookRate(@RequestParam("bookId") Integer bookId, @RequestParam("value") Integer value) {
        ResultResponse result = new ResultResponse(true, "");
        User user = userService.getUserbyId(1);
        if(user == null) {
            result.setResult(false);
            result.setError("Не удалось определить пользователя");
            return result;
        }
        Book book = bookService.getBookById(bookId);
        if(book == null) {
            result.setResult(false);
            result.setError("Не найдена книга с кодом " + bookId);
            return result;
        }
        BookReview bookReview = bookReviewService.getUserBookReview(book.getId(), user.getId());
        BookRate bookRate = bookRateService.getUserBookRate(book.getId(), user.getId());
        if(bookRate == null) {
            bookRate = new BookRate();
            bookRate.setBook(book);
            bookRate.setUser(user);
            bookRate.setTime(new Date());
            bookRate.setValue(value);
            bookRate.setBookReview(bookReview);
            Logger.getLogger(this.getClass().getSimpleName()).info("Saving rate for book " + book.getSlug() + "(id = " + book.getId() + ")");
        } else {
            bookRate.setTime(new Date());
            bookRate.setValue(value);
            Logger.getLogger(this.getClass().getSimpleName()).info("Updating rate " + bookRate.getId() + " for book " + book.getSlug() + "(id = " + book.getId() + ")");
        }
        bookRateService.save(bookRate);
        Logger.getLogger(this.getClass().getSimpleName()).info("Rate saved: " + bookRate.getId());
        return result;
    }

    @PostMapping(value = "/bookReview")
    @ResponseBody
    public ResultResponse handlePostponedBookReview(@RequestParam("bookId") Integer bookId, @RequestParam("text") String reviewText) {
        ResultResponse result = new ResultResponse(true, "");
        User user = userService.getUserbyId(1);
        if (user == null) {
            result.setResult(false);
            result.setError("Не удалось определить пользователя");
            return result;
        }
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            result.setResult(false);
            result.setError("Не найдена книга с кодом " + bookId);
            return result;
        }
        if (reviewText.isEmpty() || reviewText.length() < 10) {
            result.setResult(false);
            result.setError("Напишите, пожалуйста, более развернутый отзыв");
            return result;
        }
        BookReview bookReview = bookReviewService.getUserBookReview(book.getId(), user.getId());
        if(bookReview == null) {
            bookReview = new BookReview();
            bookReview.setBook(book);
            bookReview.setText(reviewText);
            bookReview.setTime(new Date());
            bookReview.setUser(user);
            Logger.getLogger(this.getClass().getSimpleName()).info("Saving review for book " + book.getSlug() + " (id = " + book.getId() + ")");
        } else {
            bookReview.setTime(new Date());
            bookReview.setText(reviewText);
            Logger.getLogger(this.getClass().getSimpleName()).info("Updating review " + bookReview.getId() + " for book " + book.getSlug() + "(id = " + book.getId() + ")");
        }
        bookReviewService.save(bookReview);
        Logger.getLogger(this.getClass().getSimpleName()).info("Review saved: " + bookReview.getId());
        return result;
    }

    @PostMapping(value = "/rateBookReview")
    @ResponseBody
    public ResultResponse handlePostponedBookReviewLike(@RequestParam("reviewid") Integer reviewId, @RequestParam("value") Integer value) {
        ResultResponse result = new ResultResponse(true, "");
        User user = userService.getUserbyId(1);
        if (user == null) {
            result.setResult(false);
            result.setError("Не удалось определить пользователя");
            return result;
        }
        BookReview bookReview = bookReviewService.getReviewById(reviewId);
        if (bookReview == null) {
            result.setResult(false);
            result.setError("Не найден отзыв " + reviewId);
            return result;
        }
        if (value != -1 && value != 1) {
            result.setResult(false);
            result.setError("Некорректное значение " + value);
            return result;
        }
        BookReviewLike bookReviewLike = bookReviewLikeService.getUserReviewLike(bookReview.getId(), user.getId());
        if (bookReviewLike == null) {
            bookReviewLike = new BookReviewLike();
            bookReviewLike.setReview(bookReview);
            bookReviewLike.setValue(value);
            bookReviewLike.setTime(new Date());
            bookReviewLike.setUser(user);
            Logger.getLogger(this.getClass().getSimpleName()).info("Saving " + (value == 1 ? "like" : "dislike") + " for review " + bookReview.getId());
        } else {
            bookReviewLike.setValue(value);
            bookReviewLike.setTime(new Date());
            Logger.getLogger(this.getClass().getSimpleName()).info("Updating values like for review " + bookReview.getId());
        }
        bookReviewLikeService.save(bookReviewLike);
        Logger.getLogger(this.getClass().getSimpleName()).info("Review " + bookReview.getId() +
                " for book " + bookReview.getBook().getSlug() + "(id = " + bookReview.getBook().getId() + ") " + (value == 1 ? "liked" : "disliked"));
        return result;
    }

}