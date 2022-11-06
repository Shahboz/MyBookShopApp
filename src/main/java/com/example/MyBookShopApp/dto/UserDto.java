package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.User;
import com.example.MyBookShopApp.utils.DateFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Integer id;
    private String hash;
    private String regTime;
    private Float balance;
    private String name;
    private String login;
    private Boolean setPassword;

    public UserDto(User user) {
        this.id = user.getId();
        this.hash = user.getHash();
        this.regTime = DateFormatter.format(user.getRegTime());
        this.balance = user.getBalance();
        this.name = user.getName();
        this.login = user.getEmail();
        this.setPassword = !StringUtils.isEmpty(user.getPassword());
    }

}