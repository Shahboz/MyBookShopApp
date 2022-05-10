package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.TransactionRepository;
import com.example.MyBookShopApp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Page<Transaction> getPageOfUserTransactions(Integer userdId, Integer offset, Integer limit, String sort) {
        Pageable nextPage = PageRequest.of(offset/limit, limit);
        return (sort.equalsIgnoreCase("asc") ?
                transactionRepository.findTransactionsByUserIdOrderByTimeDesc(userdId, nextPage) :
                transactionRepository.findTransactionsByUserIdOrderByTimeAsc(userdId, nextPage));
    }

}