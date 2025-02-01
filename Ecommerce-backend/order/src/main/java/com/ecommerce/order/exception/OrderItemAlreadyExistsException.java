package com.ecommerce.order.exception;

public class OrderItemAlreadyExistsException extends RuntimeException {
    public OrderItemAlreadyExistsException(String message) {
        super(message);
    }
}
