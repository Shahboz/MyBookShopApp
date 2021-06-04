package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = false)
    private Date pubDate;

    @Column(columnDefinition="SMALLINT", nullable = false)
    private Integer isBestseller;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    private String image;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition="smallint default 0", nullable = false)
    private Integer discount;

    @OneToMany(mappedBy = "book",cascade = CascadeType.REMOVE)
    private List<AuthorBooks> authorList = new ArrayList<>();

    public Book(Date pubDate, Integer isBestseller, String slug, String title, Integer price, Integer discount) {
        this.pubDate = pubDate;
        this.isBestseller = isBestseller;
        this.slug = slug;
        this.title = title;
        this.price = price;
        this.discount = discount;
    }

    public List<Author> getAuthors() {
        return this.authorList.stream().map(AuthorBooks::getAuthor).collect(Collectors.toList());
    }

    public void setAuthors(List<Author> authorList) {
        int i = 1;
        for (Author author : authorList) {
            this.authorList.add(new AuthorBooks(this, author, i++));
        }
    }

    public String getAuthorName() {
        if(this.getAuthorList().size() == 0)
            return null;
        this.getAuthorList().sort(Comparator.comparing(AuthorBooks::getSortIndex));
        return this.getAuthorList().get(0).getAuthor().getName() + (this.getAuthorList().size() > 1 ? " и др." : null);
    }

}
