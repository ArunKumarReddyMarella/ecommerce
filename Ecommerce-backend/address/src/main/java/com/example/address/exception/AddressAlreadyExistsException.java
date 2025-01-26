package com.example.address.exception;

public class AddressAlreadyExistsException extends RuntimeException{
    public AddressAlreadyExistsException(String message) {
        super(message);
    }
}
