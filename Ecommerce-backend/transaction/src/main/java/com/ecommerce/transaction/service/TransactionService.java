package com.ecommerce.transaction.service;

import com.ecommerce.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TransactionService {
    Page<Transaction> getTransactions(Pageable pageable);
    Transaction getTransactionById(String transactionId);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Transaction transaction);
    void patchTransaction(String transactionId, Map<String, Object> updates);
    void deleteTransaction(String transactionId);
}
