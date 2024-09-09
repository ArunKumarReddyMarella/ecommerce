package com.ecommerce.order_items.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_items")
public class OrderItem {

    @Id
    @NotBlank(message = "Order Item ID cannot be blank")
    private String orderItemId;

    @Column(nullable = false)
    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @Column(nullable = false)
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @Column(nullable = false)
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(nullable = false)
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;
}
