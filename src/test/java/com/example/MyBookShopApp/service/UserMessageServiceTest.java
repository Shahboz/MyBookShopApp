package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.dto.UserMessageDto;
import com.example.MyBookShopApp.entity.UserMessage;
import com.example.MyBookShopApp.repository.UserMessageRepository;
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

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserMessageServiceTest {

    private UserMessage userMessage;
    private UserMessageDto userMessageDto;

    @MockBean
    private UserMessageRepository userMessageRepository;

    private final UserMessageService userMessageService;

    @Autowired
    UserMessageServiceTest(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @BeforeEach
    void setUp() {
        userMessage = new UserMessage();
        userMessage.setId(1);
        userMessage.setTime(new Date());
        userMessage.setSubject("Subject");
        userMessage.setText("Simple message");

        userMessageDto = new UserMessageDto();
        userMessageDto.setTopic("Topic");
        userMessageDto.setMessage("Message");
        userMessageDto.setName("Username");
        userMessageDto.setMail("email@email.email");
    }

    @AfterEach
    void tearDown() {
        userMessage = null;
        userMessageDto = null;
    }

    @Test
    void getAppPhone() {
        String appPhone = userMessageService.getAppPhone();

        assertFalse(StringUtils.isEmpty(appPhone));
    }

    @Test
    void getAppEmail() {
        String appEmail = userMessageService.getAppEmail();

        assertFalse(StringUtils.isEmpty(appEmail));
    }

    @Test
    void getTelegramBot() {
        String telegramBot = userMessageService.getTelegramBot();

        assertFalse(StringUtils.isEmpty(telegramBot));
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testAddMessageFromRegisterUser() {
        Mockito.doReturn(null)
                .when(userMessageRepository)
                .findUserMessageByUserIdAndSubject(Mockito.any(Integer.class), Mockito.any(String.class));

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userMessageRepository, Mockito.times(1)).save(Mockito.any(UserMessage.class));
    }

    @Test
    void testAddMessageFromAnonymousUser() {

        Mockito.doReturn(null)
                .when(userMessageRepository)
                .findUserMessageByEmailAndNameAndSubject(userMessageDto.getMail(), userMessageDto.getName(), userMessageDto.getTopic());

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userMessageRepository, Mockito.times(1)).save(Mockito.any(UserMessage.class));
    }

    @Test
    @WithUserDetails("safarov1209@gmail.com")
    void testUpdateMessage() {
        Mockito.doReturn(userMessage)
                .when(userMessageRepository)
                .findUserMessageByUserIdAndSubject(Mockito.any(Integer.class), Mockito.any(String.class));

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertTrue(resultResponse.getResult());
        assertTrue(StringUtils.isEmpty(resultResponse.getError()));

        Mockito.verify(userMessageRepository, Mockito.times(1)).save(Mockito.any(UserMessage.class));
    }

    @Test
    void testAddMessageWithEmptyTopicFail() {
        userMessageDto.setTopic(null);

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testAddMessageWithEmptyMessageFail() {
        userMessageDto.setMessage("");

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testAddMessageWithEmptyUsernameFail() {
        userMessageDto.setName(null);

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

    @Test
    void testAddMessageWithEmptyUserEmailFail() {
        userMessageDto.setMail("");

        ResultResponse resultResponse = userMessageService.saveMessage(userMessageDto);

        assertNotNull(resultResponse);
        assertFalse(resultResponse.getResult());
        assertFalse(StringUtils.isEmpty(resultResponse.getError()));
    }

}