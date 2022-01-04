package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class BooksPageDto {

    private Integer count;
    private List<Book> books;

    public BooksPageDto(List<Book> books) {
        this.books = books;
        this.count = books.size();
    }

}
