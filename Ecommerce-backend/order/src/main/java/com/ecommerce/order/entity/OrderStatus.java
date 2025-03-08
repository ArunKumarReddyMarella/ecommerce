package com.ecommerce.order.entity;

public enum OrderStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
