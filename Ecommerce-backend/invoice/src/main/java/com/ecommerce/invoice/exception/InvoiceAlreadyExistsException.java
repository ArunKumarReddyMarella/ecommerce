package com.ecommerce.invoice.exception;

public class InvoiceAlreadyExistsException extends RuntimeException {
    public InvoiceAlreadyExistsException(String message) {
        super(message);
    }
}
