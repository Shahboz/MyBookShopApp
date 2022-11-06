package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.MyBookShopApp.utils.DateFormatter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class BookInfoDto {

    private Integer id;
    private String  title;
    private String  pubDate;
    private String  description;
    private String  slug;
    private String  image;
    private Integer price;
    private Integer discount;
    private Boolean isBestseller;
    private List<Genre> genreList;
    private List<Tag>   tagList;
    private List<BookFileDto> bookFileDtoList;

    public BookInfoDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.pubDate = DateFormatter.format(book.getPubDate());
        this.description = book.getDescription();
        this.slug = book.getSlug();
        this.image = book.getImage();
        this.price = book.getPrice();
        this.discount = book.getDiscount();
        this.isBestseller = book.getIsBestseller() == 1;
    }

}