package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.repository.UserBookTypeRepository;
import com.example.MyBookShopApp.repository.UserBooksRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserBooksServiceTest {

    private String bookStatus;
    private Integer userId;
    private String bookSlug;
    private User user;
    private Book book;
    private UserBookType userBookType;
    private UserBooks userBooks;
    private List<Book> expectedBookList;
    @MockBean
    private UserBooksRepository userBooksRepository;
    @MockBean
    private UserBookTypeRepository userBookTypeRepository;
    private final UserBooksService userBooksService;

    @Autowired
    UserBooksServiceTest(UserBooksService service) {
        this.userBooksService = service;
    }

    @BeforeEach
    void setUp() {
        userId = 1;
        bookSlug = "simple";
        bookStatus = "CART";

        Role role = new Role();
        role.setId(2);
        role.setName("REGISTER");

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        user = new User();
        user.setId(userId);
        user.setHash("userHash");
        user.setName("username");
        user.setRoles(roles);

        book = new Book();
        book.setId(3);
        book.setSlug(bookSlug);
        book.setTitle("Title");

        userBookType = new UserBookType();
        userBookType.setId(4);
        userBookType.setCode(bookStatus);
        userBookType.setName("В корзине");

        userBooks = new UserBooks();
        userBooks.setId(5);
        userBooks.setUser(user);
        userBooks.setBook(book);
        userBooks.setTime(new Date());
        userBooks.setType(userBookType);

        expectedBookList = new ArrayList<>();
        for (int i = 10; i < 12; i++) {
            Book book1 = new Book();
            book1.setId(i);

            expectedBookList.add(book1);
        }
    }

    @AfterEach
    void tearDown() {
        userId = 0;
        bookSlug = null;
        bookStatus = null;
        user = null;
        book = null;
        userBooks = null;
        userBookType = null;
        expectedBookList = null;
    }

    @Test
    void testGetUserBooks() {

        Mockito.doReturn(expectedBookList)
                .when(userBooksRepository)
                .findUserBooksByUserBookType(Mockito.any(Integer.class), Mockito.any(String.class));

        List<Book> bookList = userBooksService.getUserBooks(user, bookStatus);

        assertNotNull(bookList);
        assertThat(expectedBookList).hasSize(2);
        assertEquals(10, expectedBookList.get(0).getId().intValue());
        assertEquals(11, expectedBookList.get(1).getId().intValue());
    }

    @Test
    void testGetEmptyUserBooks() {
        List<Book> bookList = userBooksService.getUserBooks(null, bookStatus);

        assertNotNull(bookList);
        assertThat(bookList).isEmpty();
    }

    @Test
    void testDeleteBookFromCart() {

        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "CART");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "KEPT");

        bookStatus = "Unlink";
        ResultResponse result = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userBooksRepository, Mockito.times(1)).delete(Mockito.any(UserBooks.class));
    }

    @Test
    void testDeleteBookFromKept() {

        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "CART");
        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "KEPT");

        bookStatus = "Unlink";
        ResultResponse result = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userBooksRepository, Mockito.times(1)).delete(Mockito.any(UserBooks.class));
    }

    @Test
    void testAddBookToCart() {
        bookStatus = "CART";

        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "PAID");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "CART");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "KEPT");
        Mockito.doReturn(userBookType)
                .when(userBookTypeRepository)
                .findUserBookTypeByCode(bookStatus);

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userBooksRepository, Mockito.times(1)).save(Mockito.any(UserBooks.class));
    }

    @Test
    void testAddBookToKept() {
        bookStatus = "KEPT";

        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "PAID");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");
        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "CART");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "KEPT");
        Mockito.doReturn(userBookType)
                .when(userBookTypeRepository)
                .findUserBookTypeByCode(bookStatus);

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userBooksRepository, Mockito.times(1)).save(Mockito.any(UserBooks.class));
    }

    @Test
    void testPaidBook() {
        bookStatus = "PAID";

        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "CART");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");
        Mockito.doReturn(userBookType)
                .when(userBookTypeRepository)
                .findUserBookTypeByCode(bookStatus);

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userBooksRepository, Mockito.times(1)).save(Mockito.any(UserBooks.class));
    }

    @Test
    void testArchivedPaidBook() {
        bookStatus = "ARCHIVED";

        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "PAID");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");
        Mockito.doReturn(userBookType)
                .when(userBookTypeRepository)
                .findUserBookTypeByCode(bookStatus);

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userBooksRepository, Mockito.times(1)).save(Mockito.any(UserBooks.class));
    }

    @Test
    void testAddPaidBookToCartFail() {
        bookStatus = "Cart";

        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "PAID");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testPayBookFail() {
        bookStatus = "Paid";

        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "CART");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testArchivedBookFail() {
        bookStatus = "Kept";

        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "PAID");
        Mockito.doReturn(userBooks)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testArchivedNotPaidBookFail() {
        bookStatus = "ARCHIVED";

        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "PAID");
        Mockito.doReturn(null)
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(userId, bookSlug, "ARCHIVED");

        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testChangeBookToEmptyStatus() {
        ResultResponse resultResponse = userBooksService.changeBookStatus(null, book, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testChangeEmptyBookStatus() {
        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, null, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testChangeBookStatusAnonymousUser() {
        ResultResponse resultResponse = userBooksService.changeBookStatus(bookStatus, book, null);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testChangeBookToIncorrectStatus() {
        ResultResponse resultResponse = userBooksService.changeBookStatus("INCORRECT", book, user);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

}