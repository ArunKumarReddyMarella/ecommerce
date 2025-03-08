package com.ecommerce.transaction.Controller;

import com.ecommerce.transaction.controller.TransactionController;
import com.ecommerce.transaction.entity.Transaction;
import com.ecommerce.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {
    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private TransactionController transactionController;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTransactionsByDesc() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        String sortField = "transactionDate";
        Sort sort = Sort.by(Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactions = transactionService.getTransactions(pageable);
        ResponseEntity<Page<Transaction>> response = transactionController.getTransactions(page, size, sortDirection, sortField);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    public void testGetTransactionsByAsc() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        String sortField = "transactionDate";
        Sort sort = Sort.by(Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactions = transactionService.getTransactions(pageable);
        ResponseEntity<Page<Transaction>> response = transactionController.getTransactions(page, size, sortDirection, sortField);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    private static Page<Transaction> getTransactions(Pageable pageable) {
        List<Transaction> transactions=new ArrayList<>();
        Transaction transaction=new Transaction();
        transaction.setTransactionId("1");
        transaction.setOrderId("1");
        transaction.setCardId("1");
        transaction.setAmount(new BigDecimal(100));
        transactions.add(transaction);
        Transaction transaction1=new Transaction();
        transaction1.setTransactionId("2");
        transaction1.setOrderId("2");
        transaction1.setCardId("2");
        transaction1.setAmount(new BigDecimal(200));
        transactions.add(transaction1);
        return new PageImpl<>(transactions, pageable, transactions.size());
    }

    private static Transaction getTransaction() {
        Transaction transaction=new Transaction();
        transaction.setTransactionId("1");
        transaction.setOrderId("1");
        transaction.setCardId("1");
        transaction.setAmount(new BigDecimal(100));
        return transaction;
    }

    @Test
    public void testTransactionById() {
        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = getTransaction();
        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);
        ResponseEntity<Transaction> response = transactionController.getTransactionById(transactionId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    public void testCreateTransaction() {
        Transaction transaction = getTransaction();
        when(transactionService.createTransaction(transaction)).thenReturn(transaction);
        ResponseEntity<Transaction> response = transactionController.createTransaction(transaction);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
        verify(transactionService).createTransaction(transaction);
    }

    @Test
    public void testDeleteTransaction() {
        String transactionId = UUID.randomUUID().toString();
        ResponseEntity<String> response = transactionController.deleteTransaction(transactionId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transaction deleted successfully!", response.getBody());
        verify(transactionService).deleteTransaction(transactionId);
    }
    @Test
    public void testUpdateTransaction() {
        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = getTransaction();
        when(transactionService.updateTransaction(transaction)).thenReturn(transaction);
        ResponseEntity<Transaction> response = transactionController.updateTransaction(transactionId, transaction);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
        verify(transactionService).updateTransaction(transaction);
    }

   /* @Test
    void testPatchTransaction() {
        String transactionId = UUID.randomUUID().toString();
        Map<String, Object> updates = new HashMap<>();
        updates.put("orderId", "newOrderId");
        when(transactionService.patchTransaction(transactionId, updates)).thenReturn(getTransaction());
        ResponseEntity<Transaction> response = transactionController.patchTransaction(transactionId, updates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getTransaction(), response.getBody());
        verify(transactionService).patchTransaction(transactionId, updates);
    }*/
}
