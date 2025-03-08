package com.ecommerce.wishlist.service;

import com.ecommerce.wishlist.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishlistService {
    Page<Wishlist> getWishlists(Pageable pageable);

    Wishlist getWishlistById(String id);

    Wishlist createWishlist(Wishlist wishlist);

    void deleteWishlist(String id);

    Wishlist updateWishlist(Wishlist updatedWishlist);

    Page<Wishlist> getWishlistByUserId(String userId, Pageable pageable);
}
