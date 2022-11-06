package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.service.UserContactService;
import com.example.MyBookShopApp.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;


@Service
public class BookstoreUserRegister {

    private final UserService userService;
    private final UserContactService userContactService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;

    @Autowired
    public BookstoreUserRegister(UserService userService, UserContactService userContactService, BookstoreUserDetailsService bookstoreUserDetailsService,
                                 PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.userService = userService;
        this.userContactService = userContactService;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String encodePassword(String password) {
        return StringUtils.isEmpty(password) ? "" : passwordEncoder.encode(password);
    }

    public User registerNewUser(RegistrationForm registrationForm) {
        User userByEmail = userService.getUserByEmail(registrationForm.getEmail());
        User userByPhone = userService.getUserByPhone(registrationForm.getPhone());
        User user = userByPhone != null ? userByPhone : userByEmail;

        if (userByEmail == null && userByPhone == null) {
            user = new User();
            user.setHash(encodePassword(registrationForm.getName()));
            user.setRegTime(new Date());
            user.setBalance(0F);
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPassword(encodePassword(registrationForm.getPass()));
            userService.save(user);
        } else {
            // Обновим данные анонимного пользователя
            if (!StringUtils.isEmpty(user.getName()) && user.getName().contains("anonymousUser") && StringUtils.isEmpty(user.getPassword())) {
                user.setName(registrationForm.getName());
                if (!registrationForm.getName().contains("anonymousUser")) {
                    user.addRole(userService.getUserRoleByName("REGISTER"));
                }
                user.setHash(encodePassword(registrationForm.getName()));
                user.setEmail(registrationForm.getEmail());
                user.setPassword(encodePassword(registrationForm.getPass()));
                user.setRegTime(new Date());
                userService.save(user);
            }
        }

        if (!StringUtils.isEmpty(registrationForm.getEmail())) {
            UserContact emailContact = userContactService.getUserContactByContact(registrationForm.getEmail());
            if (emailContact == null) {
                emailContact = new UserContact(user, "EMAIL", registrationForm.getEmail());
            } else {
                emailContact.setUser(user);
            }
            userContactService.save(emailContact);
        }

        if (!StringUtils.isEmpty(registrationForm.getPhone())) {
            UserContact phoneContact = userContactService.getUserContactByContact(registrationForm.getPhone());
            if (phoneContact == null) {
                phoneContact = new UserContact(user, "PHONE", registrationForm.getPhone());
            } else {
                phoneContact.setUser(user);
            }
            userContactService.save(phoneContact);
        }

        return user;
    }

    public ResultResponse login(ContactConfirmationPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResultResponse response = new ResultResponse(true, "");
        return response;
    }

    public ContactConfirmationResponse jwtlogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse(jwtToken);
        return response;
    }

    public ContactConfirmationResponse jwtloginByPhoneNumber(ContactConfirmationPayload payload) {
        UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse(jwtToken);
        return response;
    }

    public Object getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof BookstoreUserDetails) {
                BookstoreUserDetails userDetails = (BookstoreUserDetails) principal;
                return userService.getUserByHash(userDetails.getUser().getHash());
            } else if (principal instanceof CustomOAuth2User) {
                CustomOAuth2User oAuth2User = (CustomOAuth2User) principal;
                return userService.getUserByEmail(oAuth2User.getEmail());
            }
        }
        return null;
    }

    public List<UserContact> getUserContacts() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof BookstoreUserDetails) {
            BookstoreUserDetails userDetails = (BookstoreUserDetails) principal;
            return userContactService.getApprovedUserContacts(userDetails.getUser().getHash());
        } else if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) principal;
            return userContactService.getApprovedUserContacts(userService.getUserByEmail(oAuth2User.getEmail()).getHash());
        }
        return null;
    }

    public ResultResponse approveContact(ContactConfirmationPayload contactConfirmation) {
        ResultResponse response = new ResultResponse();
        UserContact userContact = userContactService.getUserContactByContact(contactConfirmation.getContact());

        if (userContact == null) {
            response.setResult(false);
            response.setError("Не найден контакт");
        } else if (userContactService.isExhausted(userContact)) {
            response.setResult(false);
            response.setReturn(true);
            response.setError("Число попыток подтверждения превышено, повторите попытку через " + userContactService.getExceedMinuteRetryTimeout(userContact) + " минут");
        } else if (userContactService.isExpired(userContact)) {
            response.setResult(false);
            response.setReturn(true);
            response.setError("Код подтверждения устарел. Запросите новый код");
        } else if (userContact.getCode().equals(contactConfirmation.getCode())) {
            // Удаление существующих контактов
            List<UserContact> userContacts = userContactService.getUserContactsByType(userContact.getUser().getHash(), userContact.getType());
            for (UserContact contact : userContacts) {
                if (!contact.getContact().equalsIgnoreCase(userContact.getContact())) {
                    userContactService.delete(contact);
                }
            }
            userContact.setApproved(1);
            userContact.setCodeTrials(userContact.getCodeTrials() + 1);
            userContactService.save(userContact);

            response.setResult(true);
        } else {
            response.setResult(false);
            userContact.setCodeTrials(userContact.getCodeTrials() + 1);
            userContactService.save(userContact);

            response.setError("Код подтверждения введен неверно. У вас осталось " + userContactService.getExceedRetryCount(userContact) + " попыток");
        }
        return response;
    }

    public ResultResponse verifyCode(ContactConfirmationPayload contactConfirmation) {
        ResultResponse response = new ResultResponse();
        UserContact userContact = userContactService.getUserContactByContact(contactConfirmation.getContact());

        if (userContact == null) {
            response = null;
        } else if (userContactService.isExhausted(userContact)) {
            response.setResult(false);
            response.setReturn(true);
            if (userContact.getType().equals("EMAIL")) {
                response.setError("Количество попыток входа по e-mail исчерпано, попробуйте войти по телефону или повторить вход по e-mail через "
                        + userContactService.getExceedMinuteRetryTimeout(userContact) + " минут");
            } else {
                response.setError("Количество попыток входа по телефону исчерпано, попробуйте войти по e-mail или повторить вход по телефону через "
                        + userContactService.getExceedMinuteRetryTimeout(userContact) + " минут");
            }
        } else if (userContactService.isExpired(userContact)) {
            response.setResult(false);
            response.setReturn(true);
            response.setError("Код подтверждения устарел. Запросите новый код");
        } else {
            userContact.setCodeTrials(userContact.getCodeTrials() + 1);
            userContactService.save(userContact);

            if (userContact.getCode().equals(contactConfirmation.getCode())) {
                response.setResult(true);
            } else {
                response.setResult(false);
                response.setError("Код подтверждения введён неверно. У вас осталось " + userContactService.getExceedRetryCount(userContact) + " попыток");
            }
        }
        return response;
    }

    public ResultResponse updatePassword(String userHash, String pass) {
        ResultResponse resultResponse = new ResultResponse();
        User user = userService.getUserByHash(userHash);
        if (user == null) {
            resultResponse.setResult(false);
            resultResponse.setError("Пользователь не найден");
        } else {
            String updatePass = encodePassword(pass);
            if (StringUtils.isEmpty(user.getPassword()) || !user.getPassword().equals(updatePass)) {
                user.setPassword(updatePass);
                userService.save(user);
            }
            resultResponse.setResult(true);
        }
        return resultResponse;
    }

}