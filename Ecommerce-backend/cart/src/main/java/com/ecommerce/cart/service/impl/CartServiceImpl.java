package com.ecommerce.cart.service.impl;

import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.exception.CartAlreadyExistsException;
import com.ecommerce.cart.exception.CartNotFoundException;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.cart.service.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));
    }

    @Override
    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
    }

    @Override
    public Cart createCart(Cart cart) {
        if (cart.getCartId() == null) {
            cart.setCartId(UUID.randomUUID().toString());
        } else {
            Optional<Cart> existingCart = cartRepository.findById(cart.getCartId());
            if (existingCart.isPresent()) {
                throw new CartAlreadyExistsException("Cart with ID " + cart.getCartId() + " already exists.");
            }
        }
        return cartRepository.saveAndFlush(cart);
    }

    @Override
    public void deleteCart(String cartId) {
        if(!cartRepository.existsById(cartId)) throw new CartNotFoundException("Cart not found with ID: " + cartId);
        cartRepository.deleteById(cartId);
    }

    @Override
    public Cart updateCart(Cart updatedCart) {
        if(!cartRepository.existsById(updatedCart.getCartId())) throw new CartNotFoundException("Cart not found with ID: " + updatedCart.getCartId());
        return cartRepository.saveAndFlush(updatedCart);
    }

    @Override
    public Cart patchCart(String cartId, Map<String, Object> updates) {
        Cart existingCart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingCart, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }
        return cartRepository.save(existingCart);
    }
}
