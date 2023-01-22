package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.repository.BookRateRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class BookRateServiceTest {

    private String bookSlug;
    private Book book;
    private BookReview bookReview;
    @MockBean
    private BookRateRepository bookRateRepository;

    private final BookRateService bookRateService;

    @Autowired
    BookRateServiceTest(BookRateService bookRateService) {
        this.bookRateService = bookRateService;
    }

    @BeforeEach
    void setUp() {
        bookSlug = "BookSlug";

        book = new Book();
        book.setId(1);
        book.setSlug(bookSlug);

        bookReview = new BookReview();
        bookReview.setId(2);
        bookReview.setBook(book);
        bookReview.setTime(new Date());
        bookReview.setText("Simple review");
    }

    @AfterEach
    void tearDown() {
        bookSlug = null;
        book = null;
        bookReview = null;
    }

    @Test
    void testGetBookRates() {

        Mockito.doReturn(new ArrayList<>())
                .when(bookRateRepository)
                .findBookRatesByBookSlugAndValueEquals(Mockito.any(String.class), Mockito.any(Integer.class));

        Rate[] rates = bookRateService.getBookRates(bookSlug);

        assertNotNull(rates);
        assertThat(rates).hasSize(5);
    }

    @Test
    void testGetRatesFromEmptyBook() {
        Rate[] rates = bookRateService.getBookRates(null);

        assertNotNull(rates);
        assertThat(rates).isEmpty();
    }

    @Test
    void testGetBookRateCount() {

        Mockito.doReturn(2)
                .when(bookRateRepository)
                .countByBookSlug(bookSlug);

        Integer rateCount = bookRateService.getBookRateCount(bookSlug);

        assertNotNull(rateCount);
        assertTrue(rateCount > 0);
    }

    @Test
    void testGetEmptyBookCount() {
        Integer rateCount = bookRateService.getBookRateCount("");

        assertNotNull(rateCount);
        assertEquals(0, rateCount.intValue());
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testAddRateBook() {

        Mockito.doReturn(null)
                .when(bookRateRepository)
                .findBookRateByBookIdAndUserId(Mockito.any(Integer.class), Mockito.any());

        ResultResponse resultResponse = bookRateService.saveRateBook(book, bookReview, 5);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(bookRateRepository, Mockito.times(1)).save(Mockito.any(BookRate.class));
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testUpdateRateBook() {
        Mockito.doReturn(new BookRate())
                .when(bookRateRepository)
                .findBookRateByBookIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        ResultResponse resultResponse = bookRateService.saveRateBook(book, bookReview, 5);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(bookRateRepository, Mockito.times(1)).save(Mockito.any(BookRate.class));
    }

    @Test
    void testAnonymousAddRateBookFail() {
        ResultResponse resultResponse = bookRateService.saveRateBook(book, bookReview, 5);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testAddRateToEmptyBookFail() {
        ResultResponse resultResponse = bookRateService.saveRateBook(null, bookReview, 5);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testCalcBookRate() {

        Mockito.doReturn(5)
                .when(bookRateRepository)
                .calcBookRate(bookSlug);

        Integer rateValue = bookRateService.calcBookRate(bookSlug);

        assertNotNull(rateValue);
        assertTrue(rateValue > 0);
    }

    @Test
    void testCalcEmptyBookRate() {
        Integer rateValue = bookRateService.calcBookRate("");

        assertNotNull(rateValue);
        assertEquals(0, rateValue.intValue());
    }

}