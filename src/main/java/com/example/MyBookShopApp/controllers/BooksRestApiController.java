package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.ApiResponse;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.exceptions.BookstoreApiWrongParameterException;
import com.example.MyBookShopApp.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api")
@Api(description = "books rest api controller")
public class BooksRestApiController {

    private final BookService bookService;

    @Autowired
    public BooksRestApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books/by-title")
    @ApiOperation("Method for get books by book's title")
    public ResponseEntity<ApiResponse<Book>> booksByTitle(@RequestParam("title") String title) throws BookstoreApiWrongParameterException {
        List<Book> bookList = bookService.getBooksByTitle(title);
        ApiResponse<Book> response = new ApiResponse<>();
        response.setDebugMessage("successful request");
        response.setMessage("data size:" + bookList.size());
        response.setStatus(HttpStatus.OK);
        response.setTimeStamp(LocalDateTime.now());
        response.setData(bookList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/by-price-range")
    public ResponseEntity<List<Book>> priceRangeBooks(@RequestParam("minPrice") Integer minPrice, @RequestParam("maxPrice") Integer maxPrice) {
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(minPrice, maxPrice));
    }

    @GetMapping("/books/with-max-price")
    public ResponseEntity<List<Book>> priceRangeBooks() {
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount());
    }

    @GetMapping("/books/bestsellers")
    @ApiOperation("Method for get bestsellers book")
    public ResponseEntity<List<Book>> bestSellersBooks() {
        return ResponseEntity.ok(bookService.getBestsellers());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Book>> handleMissingServletRequestParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing required parameters", exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse<Book>> handleBookstoreApiWrongParameterException(Exception exception) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Bad parameter value...", exception), HttpStatus.BAD_REQUEST);
    }

}