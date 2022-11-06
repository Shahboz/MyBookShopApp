package com.example.MyBookShopApp.controllers.admin;

import com.example.MyBookShopApp.service.BookReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/admin/review")
public class AdminReviewsController {

    private final BookReviewService bookReviewService;

    @Autowired
    public AdminReviewsController(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @GetMapping("/delete")
    public String deleteBookReview(@RequestParam(value = "id") Integer reviewId, @RequestParam(value = "book") String bookSlug) {
        bookReviewService.deleteReview(reviewId);
        return "redirect:/admin/book/" + bookSlug;
    }

}