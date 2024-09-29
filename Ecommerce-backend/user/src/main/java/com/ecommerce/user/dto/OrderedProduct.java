package com.ecommerce.user.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class OrderedProduct {
    private String productId;
    private String productName;
    private String productDescription;
    private BigDecimal retailPrice;
    private BigDecimal discountedPrice;
    private String brand;
    private String productRating;
    private String overallRating;
    private String imageUrls;
    private Timestamp createdAt;

    private Integer quantity;
    private BigDecimal totalPrice;
}
