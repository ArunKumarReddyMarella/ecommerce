package com.ecommerce.cart.service.impl;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.exception.CartAlreadyExistsException;
import com.ecommerce.cart.exception.CartNotFoundException;
import com.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    CartRepository cartRepository;

    @InjectMocks
    CartServiceImpl cartService;

    private static List<Cart> getCarts() {
        List<Cart> cartList = new ArrayList<>();
        Cart cart1 = new Cart();
        cart1.setCartId("1");
        cart1.setUserId("user1");
        cart1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        cartList.add(cart1);

        Cart cart2 = new Cart();
        cart2.setCartId("2");
        cart2.setUserId("user2");
        cart2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        cartList.add(cart2);

        return cartList;
    }

    private static Page<Cart> getCarts(Pageable pageable) {
        List<Cart> cartList = getCarts();
        return new PageImpl<>(cartList, pageable, cartList.size());
    }

    private static Cart getCart() {
        Cart cart = new Cart();
        cart.setCartId("1");
        cart.setUserId("user1");
        cart.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return cart;
    }

    @Test
    void testGetCarts() {
        Pageable pageable = mock(Pageable.class);
        Page<Cart> expectedCarts = getCarts(pageable);
        when(cartRepository.findAll(pageable)).thenReturn(expectedCarts);

        Page<Cart> actualCarts = cartService.getCarts(pageable);

        assertEquals(expectedCarts, actualCarts);
        for (int i = 0; i < expectedCarts.getContent().size(); i++) {
            assertCartFields(expectedCarts.getContent().get(i), actualCarts.getContent().get(i));
        }
        verify(cartRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetCartById_ExistingId() {
        String cartId = "1";
        Cart cart = getCart();
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        Cart actualCart = cartService.getCartById(cartId);

        assertCartFields(cart, actualCart);
        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void testGetCartById_NonexistentId() {
        String cartId = "1";
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.getCartById(cartId));

        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void testGetCartByUserId_ExistingUserId() {
        String userId = "user1";
        Cart cart = getCart();
        List<Cart> expectedCarts = List.of(cart);
        when(cartRepository.findByUserId(userId)).thenReturn(List.of(cart));

        List<Cart> actualCarts = cartService.getCartByUserId(userId);

        for (int i = 0; i < actualCarts.size(); i++) {
            assertCartFields(expectedCarts.get(i), actualCarts.get(i));
        }
        verify(cartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetCartByUserId_NonexistentUserId() {
        String userId = "nonExistingUser";
        List<Cart> expectedCarts = List.of();
        when(cartRepository.findByUserId(userId)).thenReturn(expectedCarts);

        assertThrows(CartNotFoundException.class, () -> cartService.getCartByUserId(userId));

        verify(cartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testCreateCart(){
        Cart cart = getCart();
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.empty());
        when(cartRepository.saveAndFlush(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart createdCart = cartService.createCart(cart);

        assertNotNull(createdCart.getCartId());
        assertCartFields(cart, createdCart);
        verify(cartRepository, times(1)).findById(cart.getCartId());
        verify(cartRepository, times(1)).saveAndFlush(any(Cart.class));
    }

    @Test
    void testCreateCart_NewCart() {
        Cart cart = getCart();
        cart.setCartId(null); // Simulate new cart without ID
        when(cartRepository.saveAndFlush(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart createdCart = cartService.createCart(cart);

        assertNotNull(createdCart.getCartId()); // Verify ID is generated
        verify(cartRepository, times(1)).saveAndFlush(any(Cart.class));
    }

    @Test
    void testCreateCart_ExistingCart() {
        Cart cart = getCart();
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        assertThrows(CartAlreadyExistsException.class, () -> cartService.createCart(cart));

        verify(cartRepository, times(1)).findById(cart.getCartId());
        verify(cartRepository, never()).saveAndFlush(any(Cart.class));
    }


    @Test
    void testDeleteCart_ExistingId() {
        String cartId = "1";
        when(cartRepository.existsById(cartId)).thenReturn(true);
        doNothing().when(cartRepository).deleteById(cartId);

        cartService.deleteCart(cartId);

        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartRepository, times(1)).deleteById(cartId);
    }

    @Test
    void testDeleteCart_NonExistingId() {
        String cartId = "1";
        when(cartRepository.existsById(cartId)).thenReturn(false);

        assertThrows(CartNotFoundException.class, () -> cartService.deleteCart(cartId));

        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartRepository, never()).deleteById(anyString());
    }


    @Test
    void testUpdateCart_Successful() {
        String cartId = "1";
        Cart updatedCart = getCart();
        when(cartRepository.existsById(cartId)).thenReturn(true);
        when(cartRepository.saveAndFlush(any(Cart.class))).thenReturn(updatedCart);

        Cart result = cartService.updateCart(updatedCart);

        assertCartFields(updatedCart, result);
        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartRepository, times(1)).saveAndFlush(updatedCart);
    }


    @Test
    void testUpdateCart_CartNotFound() {
        String cartId = "1";
        Cart updatedCart = getCart();
        when(cartRepository.existsById(cartId)).thenReturn(false);

        assertThrows(CartNotFoundException.class, () -> cartService.updateCart(updatedCart));

        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartRepository, never()).saveAndFlush(any(Cart.class));
    }



    @Test
    void testPatchCart_Successful() {
        String cartId = "1";
        Cart existingCart = getCart();
        Map<String, Object> updates = Map.of("userId", "updatedUser");
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.patchCart(cartId, updates);

        assertEquals("updatedUser", existingCart.getUserId());
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).save(existingCart);
    }

    @Test
    void testPatchCart_CartNotFound() {
        String cartId = "1";
        Map<String, Object> updates = Map.of("userId", "updatedUser");
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.patchCart(cartId, updates));

        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testPatchCart_InvalidData() {
        String cartId = "1";
        Cart existingCart = getCart();
        Map<String, Object> updates = Map.of("invalidField", "Invalid Value");
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(existingCart));

        assertThrows(IllegalArgumentException.class, () -> cartService.patchCart(cartId, updates));

        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    private void assertCartFields(Cart expected, Cart actual) {
        assertEquals(expected.getCartId(), actual.getCartId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    }
}
