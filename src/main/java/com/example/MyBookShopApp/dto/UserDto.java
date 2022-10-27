package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Book;
import com.example.MyBookShopApp.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private User user;
    private String contactPhone;
    private String contactEmail;
    private List<Book> paidBooksList;
    private List<Book> archivedBooksList;
    private List<BookReviewDto> bookReviewList;

}