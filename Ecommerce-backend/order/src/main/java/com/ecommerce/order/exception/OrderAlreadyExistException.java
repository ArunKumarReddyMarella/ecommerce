package com.ecommerce.order.exception;

public class OrderAlreadyExistException extends RuntimeException{

    public OrderAlreadyExistException(String message) {
        super(message);
    }
}
