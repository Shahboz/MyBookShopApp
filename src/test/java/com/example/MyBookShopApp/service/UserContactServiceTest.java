package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.repository.UserContactRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserContactServiceTest {

    private User user;
    private String contactType;
    private String contactEmail;
    private String contactPhone;
    private UserContact userContact;
    private List<UserContact> expectedUserContacts;
    @MockBean
    private UserContactRepository userContactRepository;
    private final UserContactService userContactService;

    @Autowired
    UserContactServiceTest(UserContactService service) {
        this.userContactService = service;
    }

    @BeforeEach
    void setUp() {
        contactType = "EMAIL";
        contactEmail = "test@test.test";
        contactPhone = "79998887766";

        user = new User();
        user.setId(1);
        user.setHash("test");

        userContact = new UserContact();
        userContact.setId(2);
        userContact.setUser(user);
        userContact.setType("PHONE");
        userContact.setApproved(1);
        userContact.setCode("0000");
        userContact.setContact(contactPhone);
        userContact.setCodeTrials(20);
        userContact.setCodeTime(new Date(0L));

        expectedUserContacts = new ArrayList<>();
        for(int i = 10; i < 12; i++) {
            UserContact contact = new UserContact();
            contact.setId(i);
            contact.setType(i % 2 == 1 ? "PHONE" : "EMAIL");
            contact.setContact(i % 2 == 1 ? contactPhone : contactEmail);
            contact.setApproved(1);
            expectedUserContacts.add(contact);
        }
    }

    @AfterEach
    void tearDown() {
        user = null;
        contactType = null;
        contactEmail = null;
        contactPhone = null;
        userContact = null;
        expectedUserContacts = null;
    }

    @Test
    void testGetUserContactsByType() {

        Mockito.doReturn(expectedUserContacts)
                .when(userContactRepository)
                .findUserContactsByUserHashAndType(user.getHash(), contactType);

        List<UserContact> userContacts = userContactService.getUserContactsByType(user.getHash(), contactType);

        assertNotNull(userContacts);
        assertFalse(userContacts.isEmpty());
        assertThat(userContacts).hasSize(2);
        assertEquals(userContacts.get(0).getContact(), contactEmail);
        assertEquals(userContacts.get(1).getContact(), contactPhone);
    }

    @Test
    void testGetApprovedUserContacts() {

        Mockito.doReturn(expectedUserContacts)
                .when(userContactRepository)
                .findUserContactsByUserHashAndApproved(user.getHash(), 1);

        List<UserContact> userContacts = userContactService.getApprovedUserContacts(user.getHash());

        assertNotNull(userContacts);
        assertFalse(userContacts.isEmpty());
        assertThat(userContacts).hasSize(2);
        assertEquals(userContacts.get(0).getContact(), contactEmail);
        assertEquals(userContacts.get(1).getContact(), contactPhone);
    }

    @Test
    void testIsCodeExhausted() {
        Boolean result = userContactService.isExhausted(userContact);

        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    void testIsCodeExpired() {
        Boolean result = userContactService.isExpired(userContact);

        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    void testIsTimeoutExceed() {
        Boolean result = userContactService.isTimeoutExceed(userContact);

        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    void testGetExceedMinuteRetryTimeout() {
        long retryTimeout = userContactService.getExceedMinuteRetryTimeout(userContact);

        assertEquals(0L, retryTimeout);
    }

    @Test
    void testGetExceedMinuteRetryCount() {
        int retryCount = userContactService.getExceedRetryCount(userContact);

        assertEquals(0, retryCount);
    }

    @Test
    void testDeleteUserContact() {
        userContactService.delete(userContact);

        Mockito.verify(userContactRepository, Mockito.times(1)).delete(Mockito.any(UserContact.class));
    }

    @Test
    void testDeleteNotApprovedContacts() {
        userContactService.deleteNotApproved(user.getId());

        Mockito.verify(userContactRepository, Mockito.times(1))
                .deleteUserContactsByUserIdAndApproved(Mockito.any(Integer.class), Mockito.any(Integer.class));
    }

}