package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.service.UserContactService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class SmsServiceTest {

    private String confirmCode;
    private ContactConfirmationPayload contactPayload;
    private UserContact emailContact;
    @MockBean
    private UserContactService userContactService;
    @MockBean
    private JavaMailSender javaMailSender;
    private final SmsService smsService;

    @Autowired
    SmsServiceTest(SmsService smsService) {
        this.smsService = smsService;
    }

    @BeforeEach
    void setUp() {
        confirmCode = "9999";

        contactPayload = new ContactConfirmationPayload();
        contactPayload.setContact("root@root.rt");
        contactPayload.setCode(confirmCode);

        emailContact = new UserContact();
        emailContact.setId(1);
        emailContact.setCode(confirmCode);
        emailContact.setCodeTime(new Date(0L));
        emailContact.setCodeTrials(20);
        emailContact.setApproved(0);
        emailContact.setUser(new User());
        emailContact.setType("EMAIL");
        emailContact.setContact(contactPayload.getContact());
    }

    @AfterEach
    void tearDown() {
        confirmCode = null;
        contactPayload = null;
        emailContact = null;
    }

    @Test
    void testSendSecretCodeToEmailSuccess() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactService)
                .getUserContactByContact(contactPayload.getContact());
        Mockito.doReturn(false)
                .when(userContactService)
                .isExhausted(emailContact);
        Mockito.doReturn(true)
                .when(userContactService)
                .isExpired(emailContact);

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactService, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testSendSecretCodeTimoutExceedSuccess() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactService)
                .getUserContactByContact(emailContact.getContact());
        Mockito.doReturn(true)
                .when(userContactService)
                .isExhausted(emailContact);
        Mockito.doReturn(true)
                .when(userContactService)
                .isTimeoutExceed(emailContact);

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactService, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testSendSecretCodeEmptyContactFail() throws NoSuchAlgorithmException {

        Mockito.doReturn(null)
                .when(userContactService)
                .getUserContactByContact(contactPayload.getContact());

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testSendSecretCodeTimoutExceededFail() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactService)
                .getUserContactByContact(contactPayload.getContact());
        Mockito.doReturn(false)
                .when(userContactService)
                .isExhausted(emailContact);
        Mockito.doReturn(false)
                .when(userContactService)
                .isExpired(emailContact);
        Mockito.doReturn(false)
                .when(userContactService)
                .isTimeoutExceed(emailContact);
        Mockito.doReturn(10L)
                .when(userContactService)
                .getExceedMinuteRetryTimeout(emailContact);

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).contains("10");
    }

    @Test
    void testSendSecretCodeExhaustedFail() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactService)
                .getUserContactByContact(contactPayload.getContact());
        Mockito.doReturn(true)
                .when(userContactService)
                .isExhausted(emailContact);
        Mockito.doReturn(false)
                .when(userContactService)
                .isTimeoutExceed(emailContact);
        Mockito.doReturn(10L)
                .when(userContactService)
                .getExceedMinuteRetryTimeout(emailContact);

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).contains("10");
    }
}