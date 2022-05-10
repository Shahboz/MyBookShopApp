package com.example.MyBookShopApp.dto;

import com.example.MyBookShopApp.entity.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class TransactionsPageDto {

    private Integer count;
    private List<Transaction> transactions;

    public TransactionsPageDto(List<Transaction> transactions) {
        this.transactions = transactions;
        this.count = transactions.size();
    }

}