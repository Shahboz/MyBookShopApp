package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.repository.UserContactRepository;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookstoreUserRegisterTests {

    private String password;
    private String userHash;
    private final BookstoreUserRegister userRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload contactConfirmationPayload;
    private User user;
    private UserContact userContact;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserContactRepository userContactRepository;

    @Autowired
    BookstoreUserRegisterTests(BookstoreUserRegister userRegister, PasswordEncoder passwordEncoder) {
        this.userRegister = userRegister;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() {
        password = "123123";
        userHash = "userHash";

        registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@mail.org");
        registrationForm.setName("Tester");
        registrationForm.setPass(password);
        registrationForm.setPhone("1231231212");

        contactConfirmationPayload = new ContactConfirmationPayload();
        contactConfirmationPayload.setContact("safarov1209@gmail.com");
        contactConfirmationPayload.setCode(password);

        user = new User();
        user.setId(1);
        user.setHash(userHash);
        user.setEmail(contactConfirmationPayload.getContact());
        user.setPassword(passwordEncoder.encode(password));

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
        userHash = null;
        password = null;
        userContact = null;
        registrationForm = null;
        contactConfirmationPayload = null;
    }

    @Test
    void encodeNotNullPassword() {
        String encodedPass = userRegister.encodePassword(password);

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

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userContactRepository, Mockito.times(2)).save(Mockito.any(UserContact.class));
    }

    @Test
    void registerNewUserFail() {
        Mockito.doReturn(new User())
                .when(userRepository)
                .findUserByEmail(registrationForm.getEmail());

        User user = userRegister.registerNewUser(registrationForm);
        assertNotNull(user);
    }

    @Test
    void jwtLoginSuccess() {

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByEmail(contactConfirmationPayload.getContact());

        ContactConfirmationResponse contactConfirmationResponse = userRegister.jwtlogin(contactConfirmationPayload);
        assertNotNull(contactConfirmationResponse);
        assertFalse(contactConfirmationResponse.getResult().isEmpty());
    }

    @Test
    void jwtFailureLogin() {

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByEmail(contactConfirmationPayload.getContact());

        contactConfirmationPayload.setCode("999");
        Exception exception = assertThrows(BadCredentialsException.class, () -> userRegister.jwtlogin(contactConfirmationPayload));
        assertTrue(exception.getMessage().contains("Bad credentials"));
    }

    @Test
    void testApproveContactSuccess() {
        userContact.setCodeTime(new Date());

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());
        Mockito.doReturn(new ArrayList<>())
                .when(userContactRepository)
                .findUserContactsByUserHashAndType(userHash, userContact.getType());

        ResultResponse result = userRegister.approveContact(contactConfirmationPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactRepository, Mockito.times(0)).delete(Mockito.any(UserContact.class));
        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testApprovedEmptyContactFail() {

        Mockito.doReturn(null)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        ResultResponse result = userRegister.approveContact(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testApprovedContactExhausted() {

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        userContact.setCodeTime(new Date());
        userContact.setCodeTrials(100);
        ResultResponse result = userRegister.approveContact(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertTrue(result.getReturn());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).matches(".*\\d+.*");
    }

    @Test
    void testApprovedContactExpired() {

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        ResultResponse result = userRegister.approveContact(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertTrue(result.getReturn());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testApprovedContactCheckCodeFail() {
        userContact.setCodeTime(new Date());
        contactConfirmationPayload.setCode("0");

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        ResultResponse result = userRegister.approveContact(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testVerifyCodeSuccess() {
        userContact.setCodeTime(new Date());

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testVerifyCodeEmptyUserContactFail() {

        Mockito.doReturn(null)
                .when(userContactRepository)
                .findUserContactByContact(Mockito.any(String.class));

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNull(result);
    }

    @Test
    void testVerifyCodeExhaustedFail() {

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        userContact.setCodeTime(new Date());
        userContact.setCodeTrials(100);

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertTrue(result.getReturn());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).matches(".*\\d+.*");
    }

    @Test
    void testVerifyCodeExpiredFail() {

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertTrue(result.getReturn());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testVerifyCodeCheckCodeFail() {

        Mockito.doReturn(userContact)
                .when(userContactRepository)
                .findUserContactByContact(contactConfirmationPayload.getContact());

        userContact.setCodeTime(new Date());
        contactConfirmationPayload.setCode("000000");
        ResultResponse result = userRegister.verifyCode(contactConfirmationPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).contains("2");

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testUpdatePasswordUserSuccess() {

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByHash(userHash);

        password = "000000";
        ResultResponse result = userRegister.updatePassword(userHash, password);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testUpdatePasswordEmptyUserFail() {

        Mockito.doReturn(null)
                .when(userRepository)
                .findUserByHash(userHash);

        ResultResponse result = userRegister.updatePassword(userHash, password);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

}