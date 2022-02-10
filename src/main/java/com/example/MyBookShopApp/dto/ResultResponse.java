package com.example.MyBookShopApp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ResultResponse {

    private Boolean result;
    private String error;

    public ResultResponse(Boolean result, String error) {
        this.result = result;
        this.error = error;
    }

}