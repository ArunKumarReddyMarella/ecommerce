package com.ecommerce.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class OrderDto {
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String status;
}
