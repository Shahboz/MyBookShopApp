package com.example.MyBookShopApp.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "book_review_like")
public class BookReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id", nullable = false)
    private BookReview review;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private Date time;

    @Check(constraints = "value in (1, -1)")
    @Column(columnDefinition = "smallint", nullable = false)
    @ApiModelProperty(notes = "1 like, -1 dislike", example = "1", required = true)
    private Integer value;

    public BookReviewLike(Integer id, BookReview review, User user, Date time, Integer value) {
        this.id = id;
        this.review = review;
        this.user = user;
        this.time = time;
        this.value = value;
    }

}