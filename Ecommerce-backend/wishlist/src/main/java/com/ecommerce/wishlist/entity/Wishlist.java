package com.ecommerce.wishlist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "wishlist")
public class Wishlist {

    @Id
    private String wishlistId;

    @Column(nullable = false)
    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @Column(nullable = false)
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
