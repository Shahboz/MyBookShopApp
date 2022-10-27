package com.example.MyBookShopApp.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    public User(Integer id, String hash, Date regTime, Float balance, String name, String email, String password) {
        this.id = id;
        this.hash = hash;
        this.regTime = regTime;
        this.balance = balance;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void addRole(Role role) {
        if (!hasRole(role.getName()))
            this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r->r.getName().equals(roleName));
    }

    public void deleteRole(Role role) {
        roles.removeIf(r -> r.getName().equals(role.getName()));
    }

    public void setHash(String hash) {
        this.hash = hash.replaceAll("[^a-zA-Z0-9]", "");
    }

}