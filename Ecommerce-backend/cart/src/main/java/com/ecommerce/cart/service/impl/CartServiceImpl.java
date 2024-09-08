package com.ecommerce.cart.service.impl;

import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.cart.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Page<Cart> getCarts(Pageable pageable) {
        return cartRepository.findAll(pageable);
    }

    @Override
    public Cart getCartById(String cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        return optionalCart.orElse(null);
    }

    @Override
    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Cart createCart(Cart cart) {
        if (cart.getCartId() == null) {
            cart.setCartId(UUID.randomUUID().toString());
        } else {
            Optional<Cart> existingCart = cartRepository.findById(cart.getCartId());
            if (existingCart.isPresent()) {
                throw new RuntimeException("Cart with ID " + cart.getCartId() + " already exists.");
            }
        }
        return cartRepository.saveAndFlush(cart);
    }

    @Override
    public void deleteCart(String cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public Cart updateCart(Cart updatedCart) {
        return cartRepository.saveAndFlush(updatedCart);
    }

    @Override
    public void patchCart(String cartId, Map<String, Object> updates) {
        Cart existingCart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));

        updates.forEach((key, value) -> {
            try {
                // Use reflection or property accessors to update specific fields based on the key
                Field field = Cart.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        // Use OffsetDateTime to parse the ISO 8601 format
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingCart, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else {
                    // Set other field types as usual
                    field.set(existingCart, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle potential exceptions (e.g., invalid field name)
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        cartRepository.save(existingCart);
    }
}
