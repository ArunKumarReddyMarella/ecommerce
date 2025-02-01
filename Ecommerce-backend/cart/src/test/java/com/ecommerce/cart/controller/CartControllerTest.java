package com.ecommerce.cart.controller;

import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void testGetCartsDesc() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Cart> expectedCarts = getCarts(pageable);

        when(cartService.getCarts(pageable)).thenReturn(expectedCarts);

        ResponseEntity<Page<Cart>> response = cartController.getCarts(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCarts, response.getBody());
        for (int i = 0; i < expectedCarts.getContent().size(); i++) {
            assertCartFields(expectedCarts.getContent().get(i), response.getBody().getContent().get(i));
        }
        verify(cartService, times(1)).getCarts(pageable);
    }
    @Test
    void testGetCartsAsc() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Cart> expectedCarts = getCarts(pageable);

        when(cartService.getCarts(pageable)).thenReturn(expectedCarts);

        ResponseEntity<Page<Cart>> response = cartController.getCarts(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCarts, response.getBody());
        for (int i = 0; i < expectedCarts.getContent().size(); i++) {
            assertCartFields(expectedCarts.getContent().get(i), response.getBody().getContent().get(i));
        }
        verify(cartService, times(1)).getCarts(pageable);
    }



    private static Page<Cart> getCarts(Pageable pageable) {
        List<Cart> cartList = new ArrayList<>();
        Cart cart1 = new Cart();
        cart1.setCartId("1");
        cart1.setUserId("user1");
        cart1.setProductId("1");
        cart1.setQuantity(2);
        cart1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        cartList.add(cart1);
        Cart cart2 = new Cart();
        cart2.setCartId("2");
        cart2.setUserId("user2");
        cart2.setProductId("1");
        cart2.setQuantity(2);
        cart2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        cartList.add(cart2);
        return new PageImpl<>(cartList, pageable, cartList.size());
    }
    private static Cart getCart() {
        Cart cart = new Cart();
        cart.setCartId("1");
        cart.setUserId("user1");
        cart.setProductId("1");
        cart.setQuantity(2);
        cart.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return cart;
    }

    @Test
    void testGetCartById() {
        String cartId = UUID.randomUUID().toString();
        Cart cart = getCart();
        when(cartService.getCartById(cartId)).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.getCartById(cartId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertCartFields(cart, Objects.requireNonNull(response.getBody()));
        verify(cartService, times(1)).getCartById(cartId);
    }

    @Test
    void testGetCartByUserId() {
        String userId = "user1";
        Cart cart = getCart();
        when(cartService.getCartByUserId(userId)).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.getCartByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertCartFields(cart, Objects.requireNonNull(response.getBody()));
        verify(cartService, times(1)).getCartByUserId(userId);
    }

    @Test
    void testCreateCart() {
        Cart cart = getCart();
        when(cartService.createCart(cart)).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.createCart(cart);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertCartFields(cart, Objects.requireNonNull(response.getBody()));
        verify(cartService, times(1)).createCart(cart);
    }

    @Test
    void testUpdateCart() {
        String cartId = UUID.randomUUID().toString();
        Cart cart = getCart();
        when(cartService.updateCart(cart)).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.updateCart(cartId, cart);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertCartFields(cart, Objects.requireNonNull(response.getBody()));
        verify(cartService, times(1)).updateCart(cart);
    }

    @Test
    void testPatchCart() {
        String cartId = "1";
        Map<String, Object> updates = Map.of("userId", "updatedUser");
        Cart cart = getCart();
        when(cartService.getCartById(cartId)).thenReturn(cart);
        Cart updatedCart = getCart();
        updatedCart.setUserId("updatedUser");
        when(cartService.patchCart(cartId, updates)).thenReturn(updatedCart);

        ResponseEntity<Cart> response = cartController.patchCart(cartId, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertCartFields(updatedCart, Objects.requireNonNull(response.getBody()));
        verify(cartService, times(1)).getCartById(cartId);
        verify(cartService, times(1)).patchCart(cartId, updates);
    }

    @Test
    void testPatchCart_NotFound() {
        String cartId = "1";
        Map<String, Object> updates = Map.of("userId", "updatedUser");
        when(cartService.getCartById(cartId)).thenReturn(null);

        assertEquals(HttpStatus.NOT_FOUND, cartController.patchCart(cartId, updates).getStatusCode());

        verify(cartService, times(1)).getCartById(cartId);
    }

    @Test
    void testDeleteCart() {
        String cartId = UUID.randomUUID().toString();

        ResponseEntity<String> response = cartController.deleteCart(cartId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cart deleted successfully!.", response.getBody());
        verify(cartService, times(1)).deleteCart(cartId);
    }

    private void assertCartFields(Cart expected, Cart actual) {
        assertEquals(expected.getCartId(), actual.getCartId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getProductId(), actual.getProductId());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    }
}
