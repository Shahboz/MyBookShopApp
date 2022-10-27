package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class BookInfoDto {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

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
    private List<AuthorBooks> authorBooksList;
    private List<BookReviewDto> bookReviewDtoList;

    public BookInfoDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.pubDate = dateFormatter.format(book.getPubDate());
        this.description = book.getDescription();
        this.slug = book.getSlug();
        this.image = book.getImage();
        this.price = book.getPrice();
        this.discount = book.getDiscount();
        this.isBestseller = book.getIsBestseller() == 1;
    }

    public Integer calcDiscountPrice() {
        return this.price - this.price * this.discount / 100;
    }

    public static Date parseDate(String date) throws ParseException {
        return dateFormatter.parse(date);
    }

}