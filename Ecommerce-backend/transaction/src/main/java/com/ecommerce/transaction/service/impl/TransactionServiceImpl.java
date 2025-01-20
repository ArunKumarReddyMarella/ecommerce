package com.ecommerce.transaction.service.impl;

import com.ecommerce.transaction.entity.Transaction;
import com.ecommerce.transaction.exception.TransactionAlreadyExistsException;
import com.ecommerce.transaction.exception.TransactionNotFoundException;
import com.ecommerce.transaction.repository.TransactionRepository;
import com.ecommerce.transaction.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Page<Transaction> getTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Transaction getTransactionById(String transactionId) {
        Transaction optionalTransaction = transactionRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));
        return optionalTransaction;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getTransactionId() == null)
            transaction.setTransactionId(UUID.randomUUID().toString());
        else {
            Optional<Transaction> existingTransaction = transactionRepository.findById(transaction.getTransactionId());
            if (existingTransaction.isPresent()) {
                throw new TransactionAlreadyExistsException("Transaction with ID " + transaction.getTransactionId() + " already exists.");
            }
        }
        return transactionRepository.saveAndFlush(transaction);
    }

    @Override
    public Transaction updateTransaction(Transaction updatedTransaction) {
        Optional<Transaction> existingTransaction = transactionRepository.findById(updatedTransaction.getTransactionId());
        if (existingTransaction.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with ID " + updatedTransaction.getTransactionId() + " not found.");
        }
        return transactionRepository.saveAndFlush(updatedTransaction);
    }

    @Override
    public void patchTransaction(String transactionId, Map<String, Object> updates) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingTransaction, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }

        transactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        if(!transactionRepository.existsById(transactionId)) throw new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
        transactionRepository.deleteById(transactionId);
    }
}
