package com.ecommerce.cart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart")
public class Cart {

    @Id
    @NotBlank(message = "Cart ID cannot be blank") // Validates that cartId is not null or empty
    private String cartId;

    @Column(nullable = false) // Ensures the column in the DB cannot be null
    @NotBlank(message = "User ID cannot be blank") // Validates that userId is not null or empty
    private String userId;

    @Column(nullable = false) // Ensures the column in the DB cannot be null
    @NotBlank(message = "Product ID cannot be blank") // Validates that productId is not null or empty
    private String productId;

    @Column(nullable = false) // Ensures the column in the DB cannot be null
    @NotNull(message = "Quantity cannot be null") // Ensures that quantity is not null
    @Min(value = 1, message = "Quantity must be at least 1") // Ensures that quantity is at least 1
    private Integer quantity;

    @CreationTimestamp
    @Column(updatable = false) // Ensures the column cannot be updated after creation
    private Timestamp createdAt;
}
