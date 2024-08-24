package com.microservice.product.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;
    private String uniqId;
    private Timestamp crawlTimestamp;
    @Column(length = 65535)
    private String productUrl;
    @Column(length = 65535)
    private String productName;
    @Column(length = 65535)
    private String categories;
    private String pid;
    private BigDecimal retailPrice;
    private BigDecimal discountedPrice;
    @Column(length = 65535)
    private String imageUrls; // Assuming imageUrls is a JSON string
    private boolean isFkAdvantageProduct;
    @Column(length = 65535)
    private String productDescription;
    private String productRating;
    private String overallRating;
    private String brand;
    @Column(length = 65535)
    private String productSpecifications;
    private int stockQuantity;
    private String quantityUnit;
    private Timestamp createdAt;

}

