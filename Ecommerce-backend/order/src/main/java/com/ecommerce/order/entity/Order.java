package com.ecommerce.order.entity;

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
@Table(name = "orders")
public class Order {

    @Id
    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp orderDate;

    @Column(nullable = false)
    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @Column(nullable = false)
    @NotBlank(message = "Status cannot be blank")
    private String status;
}
