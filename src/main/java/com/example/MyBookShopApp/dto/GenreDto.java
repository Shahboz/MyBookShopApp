package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Genre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GenreDto {

    private String slug;
    private String name;
    private String rootName;

    public GenreDto(Genre genre) {
        this.slug = genre.getSlug();
        this.name = genre.getName();
        this.rootName = genre.getRoot().getName();
    }

}