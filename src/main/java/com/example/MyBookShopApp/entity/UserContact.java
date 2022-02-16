package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_contact")
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Check(constraints = "type in ('PHONE', 'EMAIL')")
    @Column(nullable = false)
    private String type;

    @Check(constraints = "approved in (1, -1)")
    @Column(columnDefinition = "smallint default -1", nullable = false)
    private Integer approved;

    private String code;

    @Column(name = "code_trials", columnDefinition = "int default 0")
    private Integer codeTrials;

    @Column(name = "code_time", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Date codeTime;

    @Column(nullable = false)
    private String contact;

}