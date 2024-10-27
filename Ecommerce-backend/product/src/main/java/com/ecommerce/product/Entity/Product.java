package com.ecommerce.product.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "product")
public class Product {

    @Id
    private String productId;

    @NotNull(message = "Crawl timestamp is required")
    private Timestamp crawlTimestamp;

    @NotBlank(message = "Product URL is required")
    @Column(length = 65535)
    private String productUrl;

    @NotBlank(message = "Product name is required")
    @Column(length = 65535)
    private String productName;

    @NotBlank(message = "Categories are required")
    @Column(length = 65535)
    private String categories;

    @NotBlank(message = "PID is required")
    private String pid;

    @NotNull(message = "Retail price is required")
    private BigDecimal retailPrice;

    @NotNull(message = "Discounted price is required")
    private BigDecimal discountedPrice;

    @NotBlank(message = "Image URLs are required")
    @Column(length = 65535)
    private String imageUrls;

    @NotNull(message = "FK advantage product flag is required")
    private boolean isFkAdvantageProduct;

    @NotBlank(message = "Product description is required")
    @Column(length = 65535)
    private String productDescription;

    @NotBlank(message = "Product rating is required")
    private String productRating;

    @NotBlank(message = "Overall rating is required")
    private String overallRating;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Product specifications are required")
    @Column(length = 65535)
    private String productSpecifications;

    @NotNull(message = "Stock quantity is required")
    private int stockQuantity;

    @NotBlank(message = "Quantity unit is required")
    private String quantityUnit;

    @CreationTimestamp
    private Timestamp createdAt;

    @PrePersist
    public void validate() {
        if (retailPrice.compareTo(discountedPrice) < 0) {
            throw new IllegalArgumentException("Discounted price cannot be greater than retail price");
        }
    }
}

