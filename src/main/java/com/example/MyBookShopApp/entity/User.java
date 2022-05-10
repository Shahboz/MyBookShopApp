package com.example.MyBookShopApp.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String hash;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private Date regTime;

    @Column(nullable = false, columnDefinition = "float default 0")
    private Float balance;

    private String name;

    @ApiModelProperty(value = "Email user. Temporary field", example = "email@email.ru")
    private String email;

    @ApiModelProperty(value = "Password user. Temporary field")
    private String password;

    public User(Integer id, String hash, Date regTime, Float balance, String name, String email, String password) {
        this.id = id;
        this.hash = hash;
        this.regTime = regTime;
        this.balance = balance;
        this.name = name;
        this.email = email;
        this.password = password;
    }

}