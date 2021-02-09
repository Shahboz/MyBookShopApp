package com.example.MyBookShopApp.data;


public class Book {

    private Integer id;
    private String author;
    private String title;
    private String priceOld;
    private String price;

    public Integer getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getPriceOld() {
        return priceOld;
    }

    public String getPrice() {
        return price;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriceOld(String priceOld) {
        this.priceOld = priceOld;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", priceOld='" + priceOld + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

}
