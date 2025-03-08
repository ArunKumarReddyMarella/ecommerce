package com.ecommerce.transaction.controller;

import com.ecommerce.transaction.entity.Transaction;
import com.ecommerce.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "transactionDate") String sortBy) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> transactions = transactionService.getTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Transaction> getTransactionsByOrderId(@PathVariable String orderId) {
        Transaction transaction = transactionService.getTransactionsByOrderId(orderId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody @Valid Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(createdTransaction);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String transactionId, @RequestBody @Valid Transaction transaction) {
        transaction.setTransactionId(transactionId);
        Transaction updatedTransaction = transactionService.updateTransaction(transaction);
        if (updatedTransaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTransaction);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<Transaction> patchTransaction(@PathVariable String transactionId, @RequestBody Map<String, Object> updates) {
        Transaction existingTransaction = transactionService.getTransactionById(transactionId);
        if (existingTransaction == null) {
            return ResponseEntity.notFound().build();
        }
        transactionService.patchTransaction(transactionId, updates);
        Transaction updatedTransaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok("Transaction deleted successfully!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
