package com.ecommerce.transaction.repository;

import com.ecommerce.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByOrderId(String orderId);
}
