package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.UserViewedBooks;
import com.example.MyBookShopApp.repository.UserViewedBooksRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@AutoConfigureMockMvc
class UserViewedBooksServiceTest {

    @MockBean
    private UserViewedBooksRepository userViewedBooksRepository;
    private final UserViewedBooksService userViewedBooksService;

    @Autowired
    UserViewedBooksServiceTest(UserViewedBooksService userViewedBooksService) {
        this.userViewedBooksService = userViewedBooksService;
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void saveBookView() {

        Mockito.doReturn(null)
                .when(userViewedBooksRepository)
                .findUserViewedBooksByUserIdAndBookId(Mockito.any(Integer.class), Mockito.any(Integer.class));

        userViewedBooksService.saveBookView(new Book());

        Mockito.verify(userViewedBooksRepository, Mockito.times(1)).save(Mockito.any(UserViewedBooks.class));
    }

}