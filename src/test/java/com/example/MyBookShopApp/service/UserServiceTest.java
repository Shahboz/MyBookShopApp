package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.UserDto;
import com.example.MyBookShopApp.entity.Role;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.repository.RoleRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {

    private String roleName;
    private String userHash;
    private Role role;
    private User user;

    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserRepository userRepository;
    private final UserService userService;

    @Autowired
    UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        roleName = "REGISTER";
        userHash = "userHash";

        role = new Role();
        role.setId(1);
        role.setName(roleName);

        user = new User();
        user.setId(2);
        user.setHash(userHash);
        user.setRegTime(new Date());
    }

    @AfterEach
    void tearDown() {
        roleName = null;
        userHash = null;
        role = null;
        user = null;
    }

    @Test
    void getUserRoleByName() {

        Mockito.doReturn(role)
                .when(roleRepository)
                .findRoleByName(roleName);

        Role role1 = userService.getUserRoleByName(roleName);

        assertNotNull(role1);
        assertEquals(role1.getName(), roleName);
    }

    @Test
    void getUserDtoByHash() {

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByHash(userHash);

        UserDto userDto = userService.getUserDtoByHash(userHash);

        assertNotNull(userDto);
        assertEquals(userDto.getHash(), userHash);
    }

    @Test
    void getEmptyUserDtoByHash() {

        Mockito.doReturn(null)
                .when(userRepository)
                .findUserByHash(userHash);

        UserDto userDto = userService.getUserDtoByHash(userHash);

        assertNotNull(userDto);
        assertTrue(StringUtils.isEmpty(userDto.getHash()));
    }

}