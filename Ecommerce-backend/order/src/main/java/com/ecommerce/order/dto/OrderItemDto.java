package com.ecommerce.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
