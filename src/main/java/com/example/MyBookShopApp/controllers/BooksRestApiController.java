package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<Book>> booksByTitle(@RequestParam("title") String title) {
        List<Book> bookList = bookService.getBooksByTitle(title);
        System.out.println("size = " + bookList.size());
        return ResponseEntity.ok(bookList);
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

}
