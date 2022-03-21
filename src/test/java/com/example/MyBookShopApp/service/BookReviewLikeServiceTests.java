package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewLikeRepository;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
public class BookReviewLikeServiceTests {

    private BookReviewLikeService reviewLikeService;
    private BookReview bookReview;
    private Integer likeCount;
    private Integer dislikeCount;

    @MockBean
    BookReviewLikeRepository reviewLikeRepository;

    @Autowired
    public BookReviewLikeServiceTests(BookReviewLikeService reviewLikeService) {
        this.reviewLikeService = reviewLikeService;
    }

    @BeforeEach
    public void setup() {
        bookReview = new BookReview();
        bookReview.setId(1);
        bookReview.setBook(new Book());
        bookReview.setTime(new Date());
        bookReview.setText("Simple review");
        bookReview.setUser(new User());

        likeCount = 100;
        dislikeCount = 50;
    }

    @AfterEach
    public void tearDown() {
        bookReview = null;
        likeCount = 0;
        dislikeCount = 0;
    }

    @Test
    public void testGetCountReviewByValue() {

        Mockito.doReturn(likeCount)
                .when(reviewLikeRepository)
                .countByReviewIdAndValueIs(bookReview.getId(), 1);

        Mockito.doReturn(dislikeCount)
                .when(reviewLikeRepository)
                .countByReviewIdAndValueIs(bookReview.getId(), -1);

        Integer likeCount = reviewLikeService.getCountReviewByValue(bookReview.getId(), 1);
        Integer dislikeCount = reviewLikeService.getCountReviewByValue(bookReview.getId(), -1);

        assertTrue(likeCount == this.likeCount);
        assertTrue(dislikeCount == this.dislikeCount);
    }

}