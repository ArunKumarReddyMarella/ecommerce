package com.ecommerce.cart.controller;

import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Page<Cart>> getCarts(
            @RequestParam(defaultValue = "0") int page,  // Default to page 0
            @RequestParam(defaultValue = "10") int size, // Default to 10 items per page
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt"); // Example: sorting by createdAt
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Cart> carts = cartService.getCarts(pageable);
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable String cartId){
        Cart cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/user/{userId}")  // Assuming user ID identifies a user's cart
    public ResponseEntity<List<Cart>> getCartByUserId(@PathVariable String userId){
        List<Cart> carts = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(carts);
    }

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody @Valid  Cart cart){
        Cart createdCart = cartService.createCart(cart);
        return ResponseEntity.ok(createdCart);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<Cart> updateCart(@PathVariable String cartId,@RequestBody @Valid Cart cart) {
//        cart.setCartId(cartId); // Ensure ID matches path variable (optional)
        Cart updatedCart = cartService.updateCart(cart);
        if (updatedCart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCart);
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<Cart> patchCart(@PathVariable String cartId, @RequestBody Map<String, Object> updates) {
        Cart existingCart = cartService.getCartById(cartId);
        if (existingCart == null) {
            return ResponseEntity.notFound().build();
        }
        Cart updatedCart = cartService.patchCart(cartId, updates); // Delegate patching logic to service
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable String cartId){
        cartService.deleteCart(cartId);
        return ResponseEntity.ok("Cart deleted successfully!.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
