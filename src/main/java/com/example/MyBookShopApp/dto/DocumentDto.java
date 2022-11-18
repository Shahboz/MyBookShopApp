package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DocumentDto {

    private String slug;
    private String title;
    private String text;
    private Integer sortIndex;

    public DocumentDto(Document document) {
        this.slug = document.getSlug();
        this.title = document.getTitle();
        this.text = document.getText();
        this.sortIndex = document.getSortIndex();
    }

}