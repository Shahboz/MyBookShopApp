package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.service.UserContactService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public void registerNewUser(RegistrationForm registrationForm) {

        if(userService.getUserByEmail(registrationForm.getEmail()) == null) {
            User user = new User();
            user.setHash(registrationForm.getName());
            user.setRegTime(new Date());
            user.setBalance(0F);
            user.setName(registrationForm.getName());
            user.setEmail(registrationForm.getEmail());
            user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            userService.save(user);

            UserContact emailContact = new UserContact();
            emailContact.setUser(user);
            emailContact.setType("EMAIL");
            emailContact.setApproved(-1);
            emailContact.setContact(registrationForm.getEmail());
            userContactService.save(emailContact);

            UserContact phoneContact = new UserContact();
            phoneContact.setUser(user);
            phoneContact.setType("PHONE");
            phoneContact.setApproved(-1);
            phoneContact.setContact(registrationForm.getPhone());
            userContactService.save(phoneContact);
        }
    }

    public ResultResponse login(ContactConfirmationPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResultResponse response = new ResultResponse(true, "");
        return response;
    }

    public ContactConfirmationResponse jwtlogin(ContactConfirmationPayload payload) {
        System.out.println("payload = 1");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));
        System.out.println("payload = 2");
        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse(jwtToken);
        return response;
    }

    public Object getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof BookstoreUserDetails) {
            BookstoreUserDetails userDetails = (BookstoreUserDetails) principal;
            return userDetails.getUser();
        }
        return null;
    }

    public List<UserContact> getUserContacts() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("user=" + principal.toString() + ", " + principal.getClass() + ", " + principal.getClass().getSimpleName());
        if(principal instanceof BookstoreUserDetails) {
            BookstoreUserDetails userDetails = (BookstoreUserDetails) principal;
            return userContactService.getUserContacts(userDetails.getUser().getId());
        }
        return null;
    }

}