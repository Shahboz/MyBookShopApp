package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PaymentDto {

    private String hash;
    private Integer sum;
    private Long time;

}