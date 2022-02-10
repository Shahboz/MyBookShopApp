package com.example.MyBookShopApp.entity;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "book_rate")
@ApiModel(description = "Data model of book rates")
public class BookRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private Date time;

    @Range(min = 1, max = 5)
    @Column(columnDefinition = "smallint", nullable = false)
    private Integer value;

    @OneToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private BookReview bookReview;

}