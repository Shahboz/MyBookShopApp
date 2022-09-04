package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.service.UserContactService;
import com.example.MyBookShopApp.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final UserContactService userContactService;

    @Autowired
    public BookstoreUserDetailsService(UserService userService, UserContactService userContactService) {
        this.userService = userService;
        this.userContactService = userContactService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(s);
        if(user != null) {
            return new BookstoreUserDetails(user);
        }
        user = userService.getUserByPhone(s);
        if (user != null) {
            return new PhoneNumberUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }

    public void processOAuthPostLogin(CustomOAuth2User customOAuth2User) {
        User user = userService.getUserByEmail(customOAuth2User.getEmail());
        if (user == null) {
            User newUser = new User();
            newUser.setName(customOAuth2User.getName());
            newUser.setEmail(customOAuth2User.getEmail());
            newUser.setBalance(0F);
            newUser.setHash(customOAuth2User.getName());
            newUser.setRegTime(new Date());
            newUser.addRole(userService.getUserRoleByName("REGISTER"));
            userService.save(newUser);

            if (!StringUtils.isEmpty(customOAuth2User.getEmail())) {
                UserContact emailContact = new UserContact();
                emailContact.setUser(newUser);
                emailContact.setType("EMAIL");
                emailContact.setApproved(-1);
                emailContact.setContact(customOAuth2User.getEmail());
                userContactService.save(emailContact);
            }

            if (!StringUtils.isEmpty(customOAuth2User.getPhone())) {
                UserContact phoneContact = new UserContact();
                phoneContact.setUser(newUser);
                phoneContact.setType("PHONE");
                phoneContact.setApproved(-1);
                phoneContact.setContact(customOAuth2User.getPhone());
                userContactService.save(phoneContact);
            }
        }
    }

}