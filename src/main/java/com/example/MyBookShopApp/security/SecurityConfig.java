package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.CustomOAuth2UserService;
import com.example.MyBookShopApp.security.jwt.JWTRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomOAuth2UserService customOAuth2UserService;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTRequestFilter filter;

    @Autowired
    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService, CustomOAuth2UserService customOAuth2UserService, JWTRequestFilter filter) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.filter = filter;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(bookstoreUserDetailsService)
            .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my", "/my/*", "/profile", "/profile/*", "/transactions", "/books/view").authenticated() //hasRole("USER")
                .antMatchers("/admin", "/admin/*").hasAuthority("ADMIN")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/signin")
                .addLogoutHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    for (Cookie cookie : httpServletRequest.getCookies()) {
                        if (cookie.getName().equals("token")) {
                            filter.getJwtUtil().deleteToken(cookie.getValue());
                        }
                    }
                }).deleteCookies("token")
                .and().oauth2Login()
                    .userInfoEndpoint().userService(customOAuth2UserService)
                .and().successHandler(new AuthenticationSuccessHandler() {

                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
                            bookstoreUserDetailsService.processOAuthPostLogin(customOAuth2User);

                            BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(customOAuth2User.getEmail());
                            Cookie cookie = new Cookie("token", filter.getJwtUtil().generateToken(userDetails));
                            cookie.setPath("/");
                            httpServletResponse.addCookie(cookie);
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                            httpServletResponse.sendRedirect("/my");
                        }
                    })
                .and().oauth2Client();

        // http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

}