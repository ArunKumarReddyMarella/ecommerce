package com.ecommerce.transaction.service.impl;

import com.ecommerce.transaction.entity.Transaction;
import com.ecommerce.transaction.repository.TransactionRepository;
import com.ecommerce.transaction.service.TransactionService;
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
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        return optionalTransaction.orElse(null);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getTransactionId() == null)
            transaction.setTransactionId(UUID.randomUUID().toString());
        else {
            Optional<Transaction> existingTransaction = transactionRepository.findById(transaction.getTransactionId());
            if (existingTransaction.isPresent()) {
                throw new RuntimeException("Transaction with ID " + transaction.getTransactionId() + " already exists.");
            }
        }
        return transactionRepository.saveAndFlush(transaction);
    }

    @Override
    public Transaction updateTransaction(Transaction updatedTransaction) {
        return transactionRepository.saveAndFlush(updatedTransaction);
    }

    @Override
    public void patchTransaction(String transactionId, Map<String, Object> updates) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = Transaction.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingTransaction, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                }
                else {
                    field.set(existingTransaction, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        transactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}
