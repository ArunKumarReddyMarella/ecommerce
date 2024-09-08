package com.ecommerce.cart.service;

import com.ecommerce.cart.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

public interface CartService {
    Page<Cart> getCarts(Pageable pageable);

    Cart getCartById(String cartId);

    Cart getCartByUserId(String userId);

    Cart createCart(Cart cart);

    void deleteCart(String cartId);

    Cart updateCart(Cart updatedCart);

    void patchCart(String cartId, Map<String, Object> updates);
}
