package com.ecommerce.wishlist.exception;

public class WishlistAlreadyExistsException extends RuntimeException {
    public WishlistAlreadyExistsException(String message) {
        super(message);
    }
}
