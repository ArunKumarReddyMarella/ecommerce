package com.ecommerce.wishlist.controller;

import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<Page<Wishlist>> getWishlists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Wishlist> wishlists = wishlistService.getWishlists(pageable);
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Wishlist> getWishlistById(@PathVariable String id){
        Wishlist wishlist = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(wishlist);
    }

    // get wishlist by user id
    @GetMapping("user/{userId}")
    public ResponseEntity<Page<Wishlist>> getWishlistByUserId(@PathVariable String userId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "desc") String sortDirection,
                                                              @RequestParam(defaultValue = "createdAt") String sortBy) {
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Wishlist> wishlists = wishlistService.getWishlistByUserId(userId, pageable);
        return ResponseEntity.ok(wishlists);
    }

    @PostMapping
    public ResponseEntity<Wishlist> createWishlist(@RequestBody Wishlist wishlist){
        Wishlist createdWishlist = wishlistService.createWishlist(wishlist);
        return ResponseEntity.ok(createdWishlist);
    }

    @PutMapping("/{wishlistId}")
    public ResponseEntity<Wishlist> updateWishlist(@PathVariable String wishlistId, @RequestBody Wishlist wishlist) {
        Wishlist updatedWishlist = wishlistService.updateWishlist(wishlist);
        if (updatedWishlist == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedWishlist);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteWishlist(@PathVariable String id){
        wishlistService.deleteWishlist(id);
        return ResponseEntity.ok("Wishlist deleted successfully!.");
    }
}

