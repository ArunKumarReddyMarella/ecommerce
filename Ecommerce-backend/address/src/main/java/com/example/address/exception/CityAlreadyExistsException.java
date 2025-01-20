package com.example.address.exception;

public class CityAlreadyExistsException extends RuntimeException {
    public CityAlreadyExistsException(String message) {
        super(message);
    }
}
