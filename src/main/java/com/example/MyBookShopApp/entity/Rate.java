package com.example.MyBookShopApp.entity;


public enum Rate {

    FIVE(5), FOUR(4), THREE(3), TWO(2), ONE(1);

    private Integer value;
    private Integer count;

    Rate(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}