package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.repository.UserRepository;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
public class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload contactConfirmationPayload;
    private User user;

    @MockBean
    private UserRepository userRepositoryMock;
    @MockBean
    private UserContactRepository userContactRepositoryMock;

    @Autowired
    public BookstoreUserRegisterTests(BookstoreUserRegister userRegister, PasswordEncoder passwordEncoder) {
        this.userRegister = userRegister;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    public void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
        registrationForm.setPass("123123");
        registrationForm.setPhone("1231231212");

        contactConfirmationPayload = new ContactConfirmationPayload();
        contactConfirmationPayload.setContact("safarov1209@gmail.com");
        contactConfirmationPayload.setCode("123123");

        user = new User();
        user.setEmail(contactConfirmationPayload.getContact());
        user.setPassword(passwordEncoder.encode(contactConfirmationPayload.getCode()));
    }

    @AfterEach
    public void tearDown() {
        user = null;
        registrationForm = null;
        contactConfirmationPayload = null;
    }

    @Test
    public void registerNewUser() {
        User user = userRegister.registerNewUser(registrationForm);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));
        assertTrue(CoreMatchers.is(user.getEmail()).matches(registrationForm.getEmail()));

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userContactRepositoryMock, Mockito.times(2)).save(Mockito.any(UserContact.class));
    }

    @Test
    public void registerNewUserFail() {
        Mockito.doReturn(new User())
                .when(userRepositoryMock)
                .findUserByEmail(registrationForm.getEmail());

        User user = userRegister.registerNewUser(registrationForm);
        assertNull(user);
    }

    @Test
    public void jwtlogin() {

        Mockito.doReturn(user)
                .when(userRepositoryMock)
                .findUserByEmail(contactConfirmationPayload.getContact());

        ContactConfirmationResponse contactConfirmationResponse = userRegister.jwtlogin(contactConfirmationPayload);
        assertNotNull(contactConfirmationResponse);
        assertTrue(!contactConfirmationResponse.getResult().isEmpty());
    }

    @Test
    public void jwtFailureLogin() {

        user.setPassword(passwordEncoder.encode(contactConfirmationPayload.getCode() + " "));

        Mockito.doReturn(user)
                .when(userRepositoryMock)
                .findUserByEmail(contactConfirmationPayload.getContact());

        Exception exception = assertThrows(BadCredentialsException.class, () -> userRegister.jwtlogin(contactConfirmationPayload));
        assertTrue(exception.getMessage().contains("Bad credentials"));
    }

}