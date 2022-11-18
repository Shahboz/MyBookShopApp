package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.repository.UserContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserContactServiceTest {

    private User user;
    private String contactType;
    private String contactEmail;
    private String contactPhone;
    private UserContactService userContactService;
    private List<UserContact> expectedUserContacts = new ArrayList<>();

    @MockBean
    private UserContactRepository userContactRepository;

    @Autowired
    UserContactServiceTest(UserContactService userContactService) {
        this.userContactService = userContactService;
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setHash("test");
        contactType = "EMAIL";
        contactEmail = "test@test.test";
        contactPhone = "79998887766";
        for(int i = 1; i <= 2; i++) {
            UserContact contact = new UserContact();
            contact.setId(i);
            contact.setType(i % 2 == 0 ? "PHONE" : "EMAIL");
            contact.setContact(i % 2 == 0 ? contactPhone : contactEmail);
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
        expectedUserContacts = null;
    }

    @Test
    public void testGetUserContactsByType() {

        Mockito.doReturn(expectedUserContacts)
                .when(userContactRepository)
                .findUserContactsByUserHashAndType(user.getHash(), contactType);

        List<UserContact> userContacts = userContactService.getUserContactsByType(user.getHash(), contactType);

        assertNotNull(userContacts);
        assertTrue(!userContacts.isEmpty());
        assertThat(userContacts).hasSize(2);
        assertEquals(userContacts.get(0).getContact(), contactEmail);
    }

    @Test
    public void testGetApprovedUserContacts() {

        Mockito.doReturn(expectedUserContacts)
                .when(userContactRepository)
                .findUserContactsByUserHashAndApproved(user.getHash(), 1);

        List<UserContact> userContacts = userContactService.getApprovedUserContacts(user.getHash());

        assertNotNull(userContacts);
        assertTrue(!userContacts.isEmpty());
        assertThat(userContacts).hasSize(2);
        assertEquals(userContacts.get(0).getContact(), contactEmail);
        assertEquals(userContacts.get(1).getContact(), contactPhone);
    }

}