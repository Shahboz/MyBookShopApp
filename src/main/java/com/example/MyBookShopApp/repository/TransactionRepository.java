package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findTransactionsByUserIdOrderByTimeDesc(Integer userId, Pageable nextPage);

    Page<Transaction> findTransactionsByUserIdOrderByTimeAsc(Integer userdId, Pageable nextPage);

}