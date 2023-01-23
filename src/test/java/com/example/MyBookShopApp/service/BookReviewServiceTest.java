package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.BookReviewData;
import com.example.MyBookShopApp.dto.BookReviewDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.BookReview;
import com.example.MyBookShopApp.entity.BookReviewLike;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.repository.BookReviewLikeRepository;
import com.example.MyBookShopApp.repository.BookReviewRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookReviewServiceTest {

    private Integer reviewId;
    private Integer bookId;
    private Book book;
    private String userHash;
    private User user;
    private String reviewText;
    private BookReview bookReview;
    private BookReviewData bookReviewData;
    private List<BookReview> expectedBookReviews;
    @MockBean
    private BookReviewRepository bookReviewRepository;
    @MockBean
    private BookReviewLikeRepository bookReviewLikeRepository;
    private final BookReviewService bookReviewService;

    @Autowired
    BookReviewServiceTest(BookReviewService service) {
        bookReviewService = service;
    }

    @BeforeEach
    void setUp() {
        reviewId = 1;
        bookId = 2;
        userHash = "userHash";
        reviewText = "Simple review text";

        book = new Book();
        book.setId(bookId);
        book.setSlug("Book");
        book.setTitle("Title");

        user = new User();
        user.setId(3);
        user.setHash(userHash);
        user.setName("Username");

        bookReview = new BookReview();
        bookReview.setId(4);
        bookReview.setBook(book);
        bookReview.setUser(user);
        bookReview.setTime(new Date());
        bookReview.setText(reviewText);

        bookReviewData = new BookReviewData();
        bookReviewData.setBookId(bookId);
        bookReviewData.setReviewid(reviewId);
        bookReviewData.setValue(1);
        bookReviewData.setText(reviewText);

        expectedBookReviews = new ArrayList<>();
        for (int i = 10; i < 12; i++) {
            BookReview review = new BookReview();
            review.setId(i);
            review.setTime(new Date());
            review.setBook(book);
            review.setUser(user);
            review.setText(reviewText + " " + i);

            expectedBookReviews.add(review);
        }
    }

    @AfterEach
    void tearDown() {
        reviewId = 0;
        bookId = 0;
        book = null;
        userHash = null;
        user = null;
        reviewText = null;
        bookReview = null;
        bookReviewData = null;
        expectedBookReviews = null;
    }

    @Test
    void getRefreshOffset() {
        Integer refreshOffset = bookReviewService.getRefreshOffset();

        assertNotNull(refreshOffset);
        assertTrue(refreshOffset > 0);
    }

    @Test
    void getRefreshLimit() {
        Integer refreshLimit = bookReviewService.getRefreshLimit();

        assertNotNull(refreshLimit);
        assertTrue(refreshLimit > 0);
    }

    @Test
    void deleteReview() {

        Mockito.doReturn(bookReview)
                .when(bookReviewRepository)
                .findBookReviewById(reviewId);

        bookReviewService.deleteReview(reviewId);

        Mockito.verify(bookReviewRepository, Mockito.times(1)).delete(Mockito.any(BookReview.class));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void testAddBookReview() {

        Mockito.doReturn(null)
                .when(bookReviewRepository)
                .findBookReviewByBookIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        ResultResponse resultResponse = bookReviewService.saveBookReview(book, reviewText);

        assertNotNull(resultResponse);
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));
        assertTrue(resultResponse.getResult());

        Mockito.verify(bookReviewRepository, Mockito.times(1)).save(Mockito.any(BookReview.class));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void testUpdateBookReview() {

        Mockito.doReturn(bookReview)
                .when(bookReviewRepository)
                .findBookReviewByBookIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        ResultResponse resultResponse = bookReviewService.saveBookReview(book, reviewText);

        assertNotNull(resultResponse);
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));
        assertTrue(resultResponse.getResult());

        Mockito.verify(bookReviewRepository, Mockito.times(1)).save(Mockito.any(BookReview.class));
    }

    @Test
    void testAnonymousUserAddBookReview() {
        ResultResponse resultResponse = bookReviewService.saveBookReview(book, reviewText);

        assertNotNull(resultResponse);
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
        assertFalse(resultResponse.getResult());

        Mockito.verify(bookReviewRepository, Mockito.times(0)).save(Mockito.any(BookReview.class));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void testAddReviewToEmptyBook() {
        ResultResponse resultResponse = bookReviewService.saveBookReview(null, reviewText);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(bookReviewRepository, Mockito.times(0)).save(Mockito.any(BookReview.class));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void testAddEmptyReviewToBook() {
        ResultResponse resultResponse = bookReviewService.saveBookReview(book, "");

        assertNotNull(resultResponse);
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
        assertFalse(resultResponse.getResult());

        Mockito.verify(bookReviewRepository, Mockito.times(0)).save(Mockito.any(BookReview.class));

        resultResponse = bookReviewService.saveBookReview(book, reviewText.substring(0, 5));

        assertNotNull(resultResponse);
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
        assertFalse(resultResponse.getResult());

        Mockito.verify(bookReviewRepository, Mockito.times(0)).save(Mockito.any(BookReview.class));
    }

    @Test
    void getBookReviewCount() {

        Mockito.doReturn(1)
                .when(bookReviewRepository)
                .countByBookSlug(book.getSlug());

        Integer countReview = bookReviewService.getBookReviewCount(book.getSlug());

        assertNotNull(countReview);
        assertEquals(1, countReview.intValue());
    }

    private void checkBookReviewDto(List<BookReviewDto> reviewDtoList) {
        assertNotNull(reviewDtoList);
        assertThat(reviewDtoList).hasSize(2);
        assertEquals(reviewDtoList.get(0).getReview().getBook(), book);
        assertEquals(reviewDtoList.get(0).getReview().getUser(), user);
        assertThat(reviewDtoList.get(0).getReview().getText()).contains(reviewText);
        assertEquals(reviewDtoList.get(1).getReview().getBook(), book);
        assertEquals(reviewDtoList.get(1).getReview().getUser(), user);
        assertThat(reviewDtoList.get(1).getReview().getText()).contains(reviewText);
    }

    @Test
    void getBookReviewDtoList() {

        Mockito.doReturn(expectedBookReviews)
                .when(bookReviewRepository)
                .findBookReviewsByBookSlug(Mockito.any(String.class), Mockito.any(Pageable.class));

        List<BookReviewDto> reviewDtoList = bookReviewService.getBookReviewDtoList(book.getSlug(), 1, 5);

        checkBookReviewDto(reviewDtoList);
    }

    @Test
    void testEmptyGetBookReviewDtoList() {
        List<BookReviewDto> reviewDtoList = bookReviewService.getBookReviewDtoList("", 1, 5);

        assertThat(reviewDtoList).isEmpty();

        reviewDtoList = bookReviewService.getBookReviewDtoList(book.getSlug(), 0, 5);

        assertThat(reviewDtoList).isEmpty();
    }

    @Test
    void getUserBookReviewDtoList() {

        Mockito.doReturn(expectedBookReviews)
                .when(bookReviewRepository)
                .findBookReviewsByUserHash(userHash);

        List<BookReviewDto> reviewDtoList = bookReviewService.getUserBookReviewDtoList(userHash);

        checkBookReviewDto(reviewDtoList);
    }

    @Test
    void testEmptyReviewGetUserBookReviewDtoList() {

        List<BookReviewDto> reviewDtoList = bookReviewService.getUserBookReviewDtoList("");

        assertNull(reviewDtoList);
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void addBookReviewLiked() {

        Mockito.doReturn(bookReview)
                .when(bookReviewRepository)
                .findBookReviewById(reviewId);

        Mockito.doReturn(null)
                .when(bookReviewLikeRepository)
                .findBookReviewLikeByReviewIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        ResultResponse resultResponse = bookReviewService.bookReviewLiked(bookReviewData);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(bookReviewLikeRepository, Mockito.times(1)).save(Mockito.any(BookReviewLike.class));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void updateBookReviewLiked() {

        Mockito.doReturn(bookReview)
                .when(bookReviewRepository)
                .findBookReviewById(reviewId);

        Mockito.doReturn(new BookReviewLike())
                .when(bookReviewLikeRepository)
                .findBookReviewLikeByReviewIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        ResultResponse resultResponse = bookReviewService.bookReviewLiked(bookReviewData);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(bookReviewLikeRepository, Mockito.times(1)).save(Mockito.any(BookReviewLike.class));
    }

    @Test
    void testAnonymousUserBookReviewLiked() {

        ResultResponse resultResponse = bookReviewService.bookReviewLiked(bookReviewData);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void testEmptyBookReviewLiked() {

        Mockito.doReturn(null)
                .when(bookReviewRepository)
                .findBookReviewById(reviewId);

        ResultResponse resultResponse = bookReviewService.bookReviewLiked(bookReviewData);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void testIncorrectValueBookReviewLike() {

        Mockito.doReturn(bookReview)
                .when(bookReviewRepository)
                .findBookReviewById(reviewId);

        bookReviewData.setValue(5);
        ResultResponse resultResponse = bookReviewService.bookReviewLiked(bookReviewData);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    @WithUserDetails("root@gmail.com")
    void getUserBookReview() {

        Mockito.doReturn(bookReview)
                .when(bookReviewRepository)
                .findBookReviewByBookIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        BookReview review = bookReviewService.getUserBookReview(bookId);

        assertNotNull(review);
        assertEquals(review.getId(), bookReview.getId());
        assertEquals(review.getBook(), bookReview.getBook());
        assertEquals(review.getUser(), bookReview.getUser());
        assertEquals(review.getText(), bookReview.getText());
    }

    @Test
    void testEmptyGetUserBookReview() {
        BookReview review = bookReviewService.getUserBookReview(bookId);

        assertNull(review);
    }

}