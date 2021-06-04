package com.example.MyBookShopApp.entity;

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

    public User(String hash, Date regTime, String name) {
        this.hash = hash;
        this.regTime = regTime;
        this.name = name;
    }

}
