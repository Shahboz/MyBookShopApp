package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.entity.UserContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
public class AuthUserController {

    private final BookstoreUserRegister userRegister;

    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister) {
        this.userRegister = userRegister;
    }

    @ModelAttribute("regFrom")
    public RegistrationForm getRegistrationForm() {
        return new RegistrationForm();
    }

    @ModelAttribute("userContacts")
    public List<UserContact> getUserContacts() {
        return userRegister.getUserContacts();
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
    public ResultResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ResultResponse response = new ResultResponse(true, "");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ResultResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ResultResponse response = new ResultResponse(true, "");
        return response;
    }

    @PostMapping("/reg")
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
        response.addCookie(cookie);
        return loginResponse;
        //return userRegister.login(payload);
    }

    @GetMapping("/my")
    public String handleMy() {
        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(@ModelAttribute("userContacts") List<UserContact> userContacts) {
        return "profile";
    }

    @GetMapping("/myarchive")
    public String handleMyArchive() {
        return "myarchive";
    }

//    @GetMapping("/logout")
//    public String handleLogout(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        SecurityContextHolder.clearContext();
//        if(session != null) {
//            session.invalidate();
//        }
//        for (Cookie cookie : request.getCookies()) {
//            cookie.setMaxAge(0);
//        }
//        return "redirect:/";
//    }

}