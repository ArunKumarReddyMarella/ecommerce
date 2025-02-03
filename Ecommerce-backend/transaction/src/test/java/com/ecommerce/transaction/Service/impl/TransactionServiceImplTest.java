package com.ecommerce.transaction.Service.impl;

import com.ecommerce.transaction.controller.TransactionController;
import com.ecommerce.transaction.entity.Transaction;
import com.ecommerce.transaction.repository.TransactionRepository;
import com.ecommerce.transaction.service.TransactionService;
import com.ecommerce.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction = new Transaction();
        transaction.setTransactionId("1");
        transaction.setOrderId("1");
        transaction.setCardId("1");
        transaction.setAmount(new BigDecimal(100));
        transactions.add(transaction);
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("2");
        transaction1.setOrderId("2");
        transaction1.setCardId("2");
        transaction1.setAmount(new BigDecimal(200));
        transactions.add(transaction1);
        return transactions;
    }

    private static Page<Transaction> getTransactions(Pageable pageable) {
        List<Transaction> transactions = getTransactions();
        Page<Transaction> expectedTransactions = new PageImpl<>(transactions, pageable, transactions.size());
        return expectedTransactions;
    }

    private static Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("1");
        transaction.setOrderId("1");
        transaction.setCardId("1");
        transaction.setAmount(new BigDecimal(100));
        return transaction;
    }

    @Test
    void testGetTransactions() {
        Pageable pageable = mock(Pageable.class);
        Page<Transaction> expectedTransactions = getTransactions(pageable);
        when(transactionRepository.findAll(pageable)).thenReturn(expectedTransactions);
        assertEquals(expectedTransactions, transactionService.getTransactions(pageable));
        verify(transactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetTransactionById_ExistindId() {
        String transactionId = "1";
        Transaction transaction = getTransaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        Transaction actualTransaction = transactionService.getTransactionById(transactionId);
        assertEquals(transaction, actualTransaction);
        verify(transactionRepository, times(1)).findById(transaction.getTransactionId());
    }

    @Test
    void testGetTransactionById_NonExistingId() {
        String transactionId = "1";
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(transactionId));
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testCreateTransaction_success() {
        Transaction transaction = getTransaction();
        transaction.setTransactionId(null);
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Transaction actualTransaction = transactionService.createTransaction(transaction);
        assertNotNull(actualTransaction.getTransactionId());
        verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
    }

    @Test
    void testCreateNewTransaction() {
        Transaction transaction = getTransaction();
        transaction.setTransactionId(null);
        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.empty());
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Transaction actualTransaction = transactionService.createTransaction(transaction);
        assertNotNull(actualTransaction.getTransactionId());
        verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_ExistingTransactionId() {
        Transaction transaction = getTransaction();
        transaction.setTransactionId("1");
        when(transactionRepository.findById(transaction.getTransactionId())).thenReturn(Optional.of(transaction));
        assertThrows(RuntimeException.class, () -> transactionService.createTransaction(transaction));
        verify(transactionRepository, times(1)).findById(transaction.getTransactionId());
    }

    @Test
    void testDeleteTransaction_ExistingTransaction() {
        String transactionId = "1";
        Transaction transaction = getTransaction();
        when(transactionRepository.existsById(transactionId)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(transactionId);
        transactionService.deleteTransaction(transactionId);
        verify(transactionRepository, times(1)).existsById(transactionId);
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void testDeleteTransaction_NonExistingTransaction() {
        String transactionId = "1";
        when(transactionRepository.existsById(transactionId)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(transactionId));
        verify(transactionRepository, times(1)).existsById(transactionId);
    }

    @Test
    void testUpdateTransaction() {
        String transactionId = "1";
        Transaction transaction = getTransaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.saveAndFlush(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Transaction actualTransaction = transactionService.updateTransaction(transaction);
        assertEquals(transaction, actualTransaction);
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).saveAndFlush(any(Transaction.class));
    }
}
