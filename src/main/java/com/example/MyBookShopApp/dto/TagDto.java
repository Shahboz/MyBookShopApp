package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TagDto {

    private String slug;
    private String name;

    public TagDto(Tag tag) {
        this.slug = tag.getSlug();
        this.name = tag.getName();
    }

}