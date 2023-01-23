package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.PaymentDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import com.example.MyBookShopApp.repository.TransactionRepository;
import com.example.MyBookShopApp.repository.UserBooksRepository;
import com.example.MyBookShopApp.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class PaymentServiceTest {

    private String userHash;
    private User user;
    private PaymentDto paymentDto;
    private List<Book> bookList;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserBooksRepository userBooksRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    private final PaymentService paymentService;

    @Autowired
    PaymentServiceTest(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @BeforeEach
    void setUp() {
        userHash = "userHash";

        paymentDto = new PaymentDto();
        paymentDto.setHash(userHash);
        paymentDto.setTime(new Date().getTime());
        paymentDto.setSum(100);

        Role role = new Role();
        role.setId(0);
        role.setName("REGISTER");
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);

        user = new User();
        user.setId(1);
        user.setHash(userHash);
        user.setBalance(100F);
        user.setRoles(roleList);

        bookList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Book book = new Book();
            book.setId(i + 10);
            book.setSlug("Slug" + i);
            book.setPrice(i);
            book.setDiscount(0);

            bookList.add(book);
        }
    }

    @AfterEach
    void tearDown() {
        userHash = null;
        user = null;
        paymentDto = null;
        bookList = null;
    }

    @Test
    void testProcessPayUserBooksSuccess() {

        Mockito.doReturn(new UserBooks())
                .when(userBooksRepository)
                .findUserBooksByUserIdAndBookSlugAndUserBookTypeCode(Mockito.any(Integer.class), Mockito.any(String.class), Mockito.any(String.class));

        ResultResponse result = paymentService.processPayUserBooks(user, bookList);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(transactionRepository, Mockito.times(5)).save(Mockito.any(Transaction.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testProcessPayOnEmptyCartFail() {
        ResultResponse result = paymentService.processPayUserBooks(user, null);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));

        result = paymentService.processPayUserBooks(user, new ArrayList<>());

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testProcessPayWithoutUserBalanceFail() {
        user.setBalance(0F);

        ResultResponse result = paymentService.processPayUserBooks(user, bookList);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testProcessPaymentSuccess() {

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByHash(userHash);

        ResultResponse result = paymentService.processPayment(paymentDto);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(transactionRepository, Mockito.times(1)).save(Mockito.any(Transaction.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testProcessPaymentNullFail() {
        ResultResponse result = paymentService.processPayment(null);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testProcessPaymentIncorrectSumFail() {
        paymentDto.setSum(0);
        ResultResponse result = paymentService.processPayment(paymentDto);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testProcessPaymentIncorrectHashFail() {
        paymentDto.setHash(null);
        ResultResponse result = paymentService.processPayment(paymentDto);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testProcessPaymentIncorrectUserHashFail() {

        Mockito.doReturn(null)
                .when(userRepository)
                .findUserByHash(userHash);

        ResultResponse result = paymentService.processPayment(paymentDto);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }
}