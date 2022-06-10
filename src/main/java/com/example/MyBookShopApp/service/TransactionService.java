package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.TransactionRepository;
import com.example.MyBookShopApp.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    @Value("${transaction.data.limit}")
    private Integer transactionLimit;

    @Value("${transaction.data.offset}")
    private Integer transactionOffset;

    @Value("${transaction.data.sort}")
    private String transactionSort;

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Integer getTransactionLimit() {
        return transactionLimit;
    }

    public Integer getTransactionOffset() {
        return transactionOffset;
    }

    public String getTransactionSort() {
        return transactionSort;
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Page<Transaction> getPageOfUserTransactions(Integer userdId, Integer offset, Integer limit, String sort) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return (sort.equalsIgnoreCase("asc") ?
                transactionRepository.findTransactionsByUserIdOrderByTimeAsc(userdId, nextPage) :
                transactionRepository.findTransactionsByUserIdOrderByTimeDesc(userdId, nextPage));
    }

}