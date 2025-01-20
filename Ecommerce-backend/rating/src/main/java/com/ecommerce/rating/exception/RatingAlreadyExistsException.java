package com.ecommerce.rating.exception;

public class RatingAlreadyExistsException extends RuntimeException {
    public RatingAlreadyExistsException(String message) {
        super(message);
    }
}
