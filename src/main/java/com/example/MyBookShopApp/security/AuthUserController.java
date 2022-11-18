package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.PaymentDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.dto.TransactionsPageDto;
import com.example.MyBookShopApp.dto.UserProfileDto;
import com.example.MyBookShopApp.entity.Transaction;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.service.PaymentService;
import com.example.MyBookShopApp.service.TransactionService;
import com.example.MyBookShopApp.service.UserContactService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;


@Controller
public class AuthUserController {

    private static final String PROFILE_PAGE = "profile";

    private final BookstoreUserRegister userRegister;
    private final SmsService smsService;
    private final PaymentService paymentService;
    private final TransactionService transactionService;
    private final UserContactService userContactService;
    private final UserService userService;

    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister, SmsService smsService, PaymentService paymentService,
                              TransactionService transactionService, UserContactService userContactService, UserService userService) {
        this.userRegister = userRegister;
        this.smsService = smsService;
        this.paymentService = paymentService;
        this.transactionService = transactionService;
        this.userContactService = userContactService;
        this.userService = userService;
    }

    @ModelAttribute("userContacts")
    public List<UserContact> getUserContacts() {
        return userRegister.getUserContacts();
    }

    @ModelAttribute("transactions")
    public List<Transaction> getUserTransactions() {
        User user = (User) userRegister.getCurrentUser();
        return user == null ? null : transactionService.getPageOfUserTransactions(user.getId(), 0, transactionService.getTransactionLimit(), transactionService.getTransactionSort()).getContent();
    }

    @ModelAttribute("dataSort")
    public String getDataTransactionSort() {
        return transactionService.getTransactionSort();
    }

    @ModelAttribute("dataOffset")
    public Integer getDataTransactionOffset() {
        return transactionService.getTransactionOffset();
    }

    @ModelAttribute("dataLimit")
    public Integer getDataTransactionLimit() {
        return transactionService.getTransactionLimit();
    }

    @GetMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TransactionsPageDto getUserTransactionsByPage(@RequestParam("offset") Integer offset,
                                                         @RequestParam("limit") Integer limit,
                                                         @RequestParam("sort") String sort) {
        User user = (User) userRegister.getCurrentUser();
        return user == null ? null : new TransactionsPageDto(transactionService.getPageOfUserTransactions(user.getId(), offset, limit, sort).getContent());
    }

    @GetMapping("/signin")
    public String handleSignIn() {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(@ModelAttribute("regForm") RegistrationForm registrationForm) {
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ResultResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) throws NoSuchAlgorithmException {
        User currentUser = (User) userRegister.getCurrentUser();
        UserContact userContact = userContactService.getUserContactByContact(payload.getContact());
        // Если пользователь не авторизован, то создаем пустого пользователя
        if (currentUser == null) {
            RegistrationForm registrationForm = new RegistrationForm();
            registrationForm.setName("anonymousUser" + SecureRandom.getInstanceStrong().nextInt(1000));
            if (payload.getContact().contains("@")) {
                registrationForm.setEmail(payload.getContact());
            } else {
                registrationForm.setPhone(payload.getContact());
            }
            userRegister.registerNewUser(registrationForm);

        } else if (userContact == null) {
            // Новый контакт пользователя
            userContact = new UserContact(currentUser, payload.getContact().contains("@") ? "EMAIL" : "PHONE", payload.getContact());
            userContactService.save(userContact);
        }
        return smsService.sendSecretCode(payload);
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ResultResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        return userRegister.approveContact(payload);
    }

    @PostMapping("/signup")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) {
        User user = userRegister.registerNewUser(registrationForm);
        model.addAttribute("regOk", user != null);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload, HttpServletResponse response) {
        ContactConfirmationResponse loginResponse = userRegister.jwtlogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ResultResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload, HttpServletResponse response) {
        ResultResponse resultResponse = userRegister.verifyCode(payload);
        if (resultResponse != null && resultResponse.getResult()) {
            ContactConfirmationResponse loginResponse = userRegister.jwtloginByPhoneNumber(payload);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
        return resultResponse;
    }

    @GetMapping("/my")
    public String handleMy() {
        User currentUser = (User) userRegister.getCurrentUser();
        return currentUser.hasRole("ADMIN") ? "redirect:/admin" : "my";
    }

    @GetMapping("/profile")
    public String handleProfile(@ModelAttribute("userContacts") List<UserContact> userContacts,
                                @ModelAttribute("transactions") List<Transaction> transactions, @ModelAttribute("dataSort") String dataSort,
                                @ModelAttribute("dataOffset") Integer dataOffset, @ModelAttribute("dataLimit") Integer dataLimit) {
        return PROFILE_PAGE;
    }

    @GetMapping("/profile/cancel")
    public String handleCancelProfile(Model model) {
        User currentUser = (User) userRegister.getCurrentUser();
        if (currentUser != null) {
            userContactService.deleteNotApproved(currentUser.getId());
            model.addAttribute("isSaved", true);
        } else {
            model.addAttribute("isSaved", false);
        }
        return PROFILE_PAGE;
    }

    @PostMapping("/profile/save")
    public String handlePostProfile(UserProfileDto userProfile, Model model) {
        String errorText = "";
        User currentUser = (User) userRegister.getCurrentUser();
        // Проверки
        if (currentUser == null) {
            errorText = "Пользователь не зарегистрирован";
        } else if (!StringUtils.isEmpty(userProfile.getPassword()) || !StringUtils.isEmpty(userProfile.getPasswordReply())) {

            if (StringUtils.isEmpty(userProfile.getPassword()) || StringUtils.isEmpty(userProfile.getPasswordReply()) ||
                    !userProfile.getPassword().equals(userProfile.getPasswordReply())
            ) {
                errorText = "Пароли не совпадают";
            } else if (userProfile.getPassword().length() != 6 || userProfile.getPasswordReply().length() != 6) {
                errorText = "Длина пароля должна быть равна 6";
            }
        }
        if (currentUser != null && StringUtils.isEmpty(errorText)) {
            Boolean isChanged = false;
            // Обновление имя пользователя
            if ((!StringUtils.isEmpty(currentUser.getName()) && !StringUtils.isEmpty(userProfile.getName()) && !userProfile.getName().equals(currentUser.getName())) ||
                    (StringUtils.isEmpty(currentUser.getName()) && !StringUtils.isEmpty(userProfile.getName()))) {
                isChanged = true;
                currentUser.setName(userProfile.getName());
                currentUser.setHash(userRegister.encodePassword(userProfile.getName()));
            }
            // Обновление Email
            if ((!StringUtils.isEmpty(currentUser.getEmail()) && !StringUtils.isEmpty(userProfile.getMail()) && !userProfile.getMail().equals(currentUser.getEmail())) ||
                    (StringUtils.isEmpty(currentUser.getEmail()) && !StringUtils.isEmpty(userProfile.getMail()))) {
                isChanged = true;
                currentUser.setEmail(userProfile.getMail());
            }
            // Обновление пароля
            String encodePassword = userRegister.encodePassword(userProfile.getPassword());
            if ((!StringUtils.isEmpty(currentUser.getPassword()) && !StringUtils.isEmpty(encodePassword) && !encodePassword.equals(currentUser.getPassword())) ||
                    (StringUtils.isEmpty(currentUser.getPassword()) && !StringUtils.isEmpty(encodePassword))) {
                isChanged = true;
                currentUser.setPassword(encodePassword);
            }
            // Сохранение
            if (isChanged) {
                userService.save(currentUser);
            }
        } else {
            model.addAttribute("errorText", errorText);
        }
        return PROFILE_PAGE;
    }

    @PostMapping("/payment")
    @ResponseBody
    public ResultResponse handlePostPayment(@RequestBody PaymentDto payment) {
        return paymentService.processPayment(payment);
    }

    @GetMapping("/my/archive")
    public String handleMyArchive() {
        return "myarchive";
    }

}