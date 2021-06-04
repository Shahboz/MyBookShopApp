package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "book2author")
public class AuthorBooks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private Author author;

    @Column(columnDefinition = "int default 0", nullable = false)
    private Integer sortIndex;

    public AuthorBooks(Book book, Author author, Integer sortIndex) {
        this.book = book;
        this.author = author;
        this.sortIndex = sortIndex;
    }

}
