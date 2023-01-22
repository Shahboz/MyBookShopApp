package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.service.UserContactService;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload contactConfirmationPayload;
    private User user;
    private UserContact userContact;

    @MockBean
    private UserRepository userRepositoryMock;
    @MockBean
    private UserContactService userContactServiceMock;

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
        registrationForm.setPass("123123");
        registrationForm.setPhone("1231231212");

        contactConfirmationPayload = new ContactConfirmationPayload();
        contactConfirmationPayload.setContact("safarov1209@gmail.com");
        contactConfirmationPayload.setCode("123123");

        user = new User();
        user.setId(1);
        user.setHash("userHash");
        user.setEmail(contactConfirmationPayload.getContact());
        user.setPassword(passwordEncoder.encode(contactConfirmationPayload.getCode()));

        userContact = new UserContact();
        userContact.setId(2);
        userContact.setUser(user);
        userContact.setCodeTime(new Date(0L));
        userContact.setApproved(1);
        userContact.setCodeTrials(0);
        userContact.setType("EMAIL");
        userContact.setContact(contactConfirmationPayload.getContact());
        userContact.setCode(contactConfirmationPayload.getCode());
    }

    @AfterEach
    void tearDown() {
        user = null;
        userContact = null;
        registrationForm = null;
        contactConfirmationPayload = null;
    }

    @Test
    void encodeNotNullPassword() {
        String encodedPass = userRegister.encodePassword(registrationForm.getPass());

        assertFalse(StringUtils.isEmpty(encodedPass));
    }

    @Test
    void encodeEmptyPassword() {
        String encodedPass = userRegister.encodePassword("");

        assertTrue(StringUtils.isEmpty(encodedPass));
    }

    @Test
    void registerNewUser() {
        User user = userRegister.registerNewUser(registrationForm);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));
        assertTrue(CoreMatchers.is(user.getEmail()).matches(registrationForm.getEmail()));

        Mockito.verify(userRepositoryMock, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userContactServiceMock, Mockito.times(2)).save(Mockito.any(UserContact.class));
    }

    @Test
    void registerNewUserFail() {
        Mockito.doReturn(new User())
                .when(userRepositoryMock)
                .findUserByEmail(registrationForm.getEmail());

        User user = userRegister.registerNewUser(registrationForm);
        assertNotNull(user);
    }

    @Test
    void jwtlogin() {

        Mockito.doReturn(user)
                .when(userRepositoryMock)
                .findUserByEmail(contactConfirmationPayload.getContact());

        ContactConfirmationResponse contactConfirmationResponse = userRegister.jwtlogin(contactConfirmationPayload);
        assertNotNull(contactConfirmationResponse);
        assertFalse(contactConfirmationResponse.getResult().isEmpty());
    }

    @Test
    void jwtFailureLogin() {

        user.setPassword(passwordEncoder.encode(contactConfirmationPayload.getCode() + " "));

        Mockito.doReturn(user)
                .when(userRepositoryMock)
                .findUserByEmail(contactConfirmationPayload.getContact());

        Exception exception = assertThrows(BadCredentialsException.class, () -> userRegister.jwtlogin(contactConfirmationPayload));
        assertTrue(exception.getMessage().contains("Bad credentials"));
    }

    @Test
    void testVerifyCodeSuccess() {
        Mockito.doReturn(userContact)
                .when(userContactServiceMock)
                .getUserContactByContact(contactConfirmationPayload.getContact());
        Mockito.doReturn(false)
                .when(userContactServiceMock)
                .isExhausted(userContact);
        Mockito.doReturn(false)
                .when(userContactServiceMock)
                .isExpired(userContact);

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactServiceMock, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testVerifyCodeEmptyUserContactFail() {

        Mockito.doReturn(null)
                .when(userContactServiceMock)
                .getUserContactByContact(Mockito.any(String.class));

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNull(result);
    }

    @Test
    void testVerifyCodeExhaustedFail() {

        Mockito.doReturn(userContact)
                .when(userContactServiceMock)
                .getUserContactByContact(contactConfirmationPayload.getContact());
        Mockito.doReturn(true)
                .when(userContactServiceMock)
                .isExhausted(userContact);
        Mockito.doReturn(10L)
                .when(userContactServiceMock)
                .getExceedMinuteRetryTimeout(userContact);

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertTrue(result.getReturn());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).contains("10");
    }

    @Test
    void testVerifyCodeExpiredFail() {

        Mockito.doReturn(userContact)
                .when(userContactServiceMock)
                .getUserContactByContact(contactConfirmationPayload.getContact());
        Mockito.doReturn(false)
                .when(userContactServiceMock)
                        .isExhausted(userContact);
        Mockito.doReturn(true)
                .when(userContactServiceMock)
                .isExpired(Mockito.any(UserContact.class));

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertTrue(result.getReturn());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testVerifyCodeCheckCodeFail() {

        Mockito.doReturn(userContact)
                .when(userContactServiceMock)
                .getUserContactByContact(contactConfirmationPayload.getContact());
        Mockito.doReturn(false)
                .when(userContactServiceMock)
                .isExhausted(userContact);
        Mockito.doReturn(false)
                .when(userContactServiceMock)
                .isExpired(userContact);
        Mockito.doReturn(3)
                .when(userContactServiceMock)
                .getExceedRetryCount(userContact);

        contactConfirmationPayload.setCode("000000");
        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).contains("3");

        Mockito.verify(userContactServiceMock, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

}