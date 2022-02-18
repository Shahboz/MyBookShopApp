package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.UserContactRepository;
import com.example.MyBookShopApp.dto.UserRepository;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;

    @MockBean
    private UserRepository userRepositoryMock;
    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @Autowired
    BookstoreUserRegisterTests(BookstoreUserRegister userRegister, PasswordEncoder passwordEncoder) {
        this.userRegister = userRegister;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
        registrationForm.setPass("123");
        registrationForm.setPhone("1231231212");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    void registerNewUser() {
        User user = userRegister.registerNewUser(registrationForm);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));
        assertTrue(CoreMatchers.is(user.getEmail()).matches(registrationForm.getEmail()));

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(2)).save(Mockito.any(UserContact.class));
    }

    @Test
    void registerNewUserFail() {
        Mockito.doReturn(new User())
                .when(userRepositoryMock)
                .findUserByEmail(registrationForm.getEmail());

        User user = userRegister.registerNewUser(registrationForm);
        assertNull(user);
    }

}