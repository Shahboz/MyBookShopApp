package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.repository.BookReviewLikeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class BookReviewLikeServiceTests {

    private BookReviewLikeService reviewLikeService;
    private BookReview bookReview;
    private BookReviewLike bookReviewLike;
    private User user;
    private Integer likeCount;
    private Integer dislikeCount;

    @MockBean
    private BookReviewLikeRepository reviewLikeRepository;

    @Autowired
    BookReviewLikeServiceTests(BookReviewLikeService reviewLikeService) {
        this.reviewLikeService = reviewLikeService;
    }

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);

        bookReview = new BookReview();
        bookReview.setId(2);
        bookReview.setBook(new Book());
        bookReview.setTime(new Date());
        bookReview.setText("Simple review");
        bookReview.setUser(user);

        bookReviewLike = new BookReviewLike();
        bookReviewLike.setId(3);
        bookReviewLike.setReview(bookReview);
        bookReviewLike.setUser(user);
        bookReview.setTime(new Date());
        bookReviewLike.setValue(1);

        likeCount = 100;
        dislikeCount = 50;
    }

    @AfterEach
    void tearDown() {
        bookReview = null;
        user = null;
        bookReviewLike = null;
        likeCount = 0;
        dislikeCount = 0;
    }

    @Test
    void testGetCountReviewByValue() {

        Mockito.doReturn(likeCount)
                .when(reviewLikeRepository)
                .countByReviewIdAndValueIs(bookReview.getId(), 1);

        Mockito.doReturn(dislikeCount)
                .when(reviewLikeRepository)
                .countByReviewIdAndValueIs(bookReview.getId(), -1);

        Integer likeCount = reviewLikeService.getCountReviewByValue(bookReview.getId(), 1);
        Integer dislikeCount = reviewLikeService.getCountReviewByValue(bookReview.getId(), -1);

        assertEquals(likeCount, this.likeCount);
        assertEquals(dislikeCount, this.dislikeCount);
    }

    @Test
    void testGetUserReviewLike() {

        Mockito.doReturn(bookReviewLike)
                .when(reviewLikeRepository)
                .findBookReviewLikeByReviewIdAndUserId(bookReview.getId(), user.getId());

        BookReviewLike bookReviewLike = reviewLikeService.getUserReviewLike(bookReview.getId(), user.getId());

        assertNotNull(bookReviewLike);
        assertEquals(bookReviewLike.getId(), this.bookReviewLike.getId());
        assertEquals(bookReviewLike.getReview(), this.bookReview);
        assertEquals(bookReviewLike.getUser(), this.user);
        assertEquals(bookReviewLike.getValue(), this.bookReviewLike.getValue());
    }

    @Test
    void testSaveBookReviewLike() {

        Mockito.doReturn(null)
                .when(reviewLikeRepository)
                .save(Mockito.any(BookReviewLike.class));

        reviewLikeService.save(this.bookReviewLike);

        Mockito.verify(reviewLikeRepository, Mockito.times(1)).save(Mockito.any(BookReviewLike.class));
    }

}