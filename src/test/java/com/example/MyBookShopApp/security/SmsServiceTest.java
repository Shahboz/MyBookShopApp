package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.repository.UserContactRepository;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
    private UserContactRepository userContactRepository;
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
        emailContact.setCodeTrials(0);
        emailContact.setApproved(1);
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
                .when(userContactRepository)
                .findUserContactByContact(contactPayload.getContact());

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testSendSecretCodeToPhoneSuccess() throws NoSuchAlgorithmException {
        contactPayload.setContact("7 999 999 99 99");

        UserContact phoneContact = new UserContact();
        phoneContact.setId(1);
        phoneContact.setCode(confirmCode);
        phoneContact.setCodeTime(new Date(0L));
        phoneContact.setCodeTrials(0);
        phoneContact.setApproved(1);
        phoneContact.setType("PHONE");
        phoneContact.setContact(contactPayload.getContact());

        Mockito.doReturn(phoneContact)
                .when(userContactRepository)
                .findUserContactByContact(phoneContact.getContact());

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testSendSecretCodeTimoutExceedSuccess() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactRepository)
                .findUserContactByContact(emailContact.getContact());

        emailContact.setCodeTrials(100);
        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertTrue(result.getResult());
        assertTrue(StringUtils.isEmpty(result.getError()));

        Mockito.verify(userContactRepository, Mockito.times(1)).save(Mockito.any(UserContact.class));
    }

    @Test
    void testSendSecretCodeEmptyContactFail() throws NoSuchAlgorithmException {

        Mockito.doReturn(null)
                .when(userContactRepository)
                .findUserContactByContact(contactPayload.getContact());

        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
    }

    @Test
    void testSendSecretCodeTimoutExceededFail() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactRepository)
                .findUserContactByContact(contactPayload.getContact());

        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(12, ChronoUnit.MINUTES));
        emailContact.setCodeTime(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).matches(".*\\d+.*");
    }

    @Test
    void testSendSecretCodeExhaustedFail() throws NoSuchAlgorithmException {

        Mockito.doReturn(emailContact)
                .when(userContactRepository)
                .findUserContactByContact(contactPayload.getContact());

        emailContact.setCodeTrials(20);
        emailContact.setCodeTime(new Date());
        ResultResponse result = smsService.sendSecretCode(contactPayload);

        assertNotNull(result);
        assertFalse(result.getResult());
        assertFalse(StringUtils.isEmpty(result.getError()));
        assertThat(result.getError()).matches(".*\\d+.*");
    }

}