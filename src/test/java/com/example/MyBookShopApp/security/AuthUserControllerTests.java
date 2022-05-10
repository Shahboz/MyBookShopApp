package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.UserContactRepository;
import com.example.MyBookShopApp.dto.UserRepository;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import javax.servlet.http.Cookie;
import java.util.Date;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class AuthUserControllerTests {

    private final MockMvc mockMvc;
    private final PasswordEncoder passwordEncoder;

    private User user;
    private RegistrationForm registrationForm;
    private ContactConfirmationPayload payload;


    @Autowired
    public AuthUserControllerTests(MockMvc mockMvc, PasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.passwordEncoder = passwordEncoder;
    }

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserContactRepository userContactRepository;

    @BeforeEach
    public void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setName("Test User");
        registrationForm.setPhone("7 000 000 00 00");
        registrationForm.setEmail("test@test.ru");
        registrationForm.setPass("12345");

        payload = new ContactConfirmationPayload();
        payload.setContact(registrationForm.getEmail());
        payload.setCode(registrationForm.getPass());

        user = new User();
        user.setId(1);
        user.setName(registrationForm.getName());
        user.setRegTime(new Date());
        user.setEmail(registrationForm.getEmail());
        user.setPassword(passwordEncoder.encode(payload.getCode()));
        user.setBalance(0F);
        user.setHash(registrationForm.getName());
    }

    @AfterEach
    public void tearDown() {
        user = null;
        payload = null;
        registrationForm = null;
    }

    @Test
    public void registerNewUser() throws Exception {
        MultiValueMap<String, String> requestparams = new LinkedMultiValueMap<>();
        requestparams.add("name", registrationForm.getName());
        requestparams.add("phone", registrationForm.getPhone());
        requestparams.add("email", registrationForm.getEmail());
        requestparams.add("pass", registrationForm.getPass());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/signup")
                .params(requestparams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("regOk"))
                .andExpect(model().attribute("regOk", true))
                .andExpect(content().string(containsString("Регистрация прошла успешно!")));

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userContactRepository, Mockito.times(2)).save(Mockito.any(UserContact.class));
    }

    @Test
    public void registerExistingUser() throws Exception {
        MultiValueMap<String, String> requestparams = new LinkedMultiValueMap<>();
        requestparams.add("name", registrationForm.getName());
        requestparams.add("phone", registrationForm.getPhone());
        requestparams.add("email", registrationForm.getEmail());
        requestparams.add("pass", registrationForm.getPass());

        Mockito.doReturn(new User())
                .when(userRepository)
                .findUserByEmail(registrationForm.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/signup")
                .params(requestparams))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("regOk"))
                .andExpect(model().attribute("regOk", false))
                .andExpect(content().string(CoreMatchers.not(containsString("Регистрация прошла успешно!"))));

        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
        Mockito.verify(userContactRepository, Mockito.times(0)).save(Mockito.any(UserContact.class));
    }

    @Test
    public void loginTest() throws Exception {

        Mockito.doReturn(this.user)
                .when(userRepository)
                .findUserByEmail(payload.getContact());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(cookie().exists("token"));
    }

    @Test
    public void loginTestFailure() throws Exception {

        Mockito.doReturn(this.user)
                .when(userRepository)
                .findUserByEmail(payload.getContact());

        payload.setCode(registrationForm.getPhone());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"))
                .andExpect(cookie().doesNotExist("token"));
    }

    @Test
    @WithMockUser(username = "admin")
    public void logoutTest() throws Exception {
        mockMvc.perform(get("/logout").cookie(new Cookie("token", "simpleCookie")))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"))
                .andExpect(cookie().value("token", (String) null));
    }

}