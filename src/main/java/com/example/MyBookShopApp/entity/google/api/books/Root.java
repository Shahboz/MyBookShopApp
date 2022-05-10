package com.example.MyBookShopApp.entity.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Root {
    @JsonProperty("kind")
    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    String kind;

    @JsonProperty("totalItems")
    public long getTotalItems() {
        return this.totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    long totalItems;

    @JsonProperty("items")
    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    List<Item> items;
}
