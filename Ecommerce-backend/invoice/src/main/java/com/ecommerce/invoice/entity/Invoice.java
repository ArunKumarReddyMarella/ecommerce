package com.ecommerce.invoice.entity;

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
@Table(name = "invoices")
public class Invoice {

    @Id
    @NotBlank(message = "Invoice ID cannot be blank")
    private String invoiceId;

    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;

    @NotNull(message = "Payment amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than zero")
    private BigDecimal paymentAmount;

    @CreationTimestamp
    private Timestamp paymentDate;
}
