package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String photo;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition="TEXT")
    private String description;

    @OneToMany(mappedBy = "author",cascade = CascadeType.REMOVE)
    private List<AuthorBooks> bookList = new ArrayList<>();

    public Author(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    public List<Book> getBooks() {
        return this.bookList.stream().map(AuthorBooks::getBook).collect(Collectors.toList());
    }

    public void setBooks(List<Book> bookList) {
        bookList.forEach(book -> this.bookList.add(new AuthorBooks(book, this, 1)));
    }

}
