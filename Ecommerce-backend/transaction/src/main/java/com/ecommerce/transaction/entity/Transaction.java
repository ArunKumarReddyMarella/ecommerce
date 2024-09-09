package com.ecommerce.transaction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {

    @Id
    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;

    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotBlank(message = "Card ID cannot be blank")
    private String cardId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @CreationTimestamp
    private Timestamp transactionDate;
}

