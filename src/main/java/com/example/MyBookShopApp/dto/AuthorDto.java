package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Author;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorDto {

    private String name;
    private String slug;
    private String photo;
    private String description;
    private Integer countBooks;

    public AuthorDto(Author author) {
        this.name = author.getName();
        this.slug = author.getSlug();
        this.photo = author.getPhoto();
        this.description = author.getDescription();
        this.countBooks = author.getBooks() == null ? 0 : author.getBooks().size();
    }

}