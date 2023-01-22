package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setName("Test user");
        user.setRegTime(new Date());
        user.setHash("Hash");
        user.setBalance(0F);
        user.setEmail("test@email.com");
        user.setPassword("123");

        assertNotNull(userRepository.save(user));
    }

}