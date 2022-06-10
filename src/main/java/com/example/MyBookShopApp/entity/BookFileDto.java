package com.example.MyBookShopApp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class BookFileDto {

    private BookFile bookFile;
    private Integer fileSize;

    public BookFileDto(BookFile bookFile, Integer fileSize) {
        this.bookFile = bookFile;
        this.fileSize = fileSize;
    }

}