package com.ecommerce.wishlist.service.impl;

import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.repository.WishlistRepository;
import com.ecommerce.wishlist.service.WishlistService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Page<Wishlist> getWishlists(Pageable pageable) {
        return wishlistRepository.findAll(pageable);
    }

    @Override
    public Wishlist getWishlistById(String id) {
        Optional<Wishlist> optionalWishlist = wishlistRepository.findById(id);
        return optionalWishlist.orElse(null);
    }

    @Override
    public Wishlist createWishlist(Wishlist wishlist) {
        if (wishlist.getWishlistId() == null) {
            wishlist.setWishlistId(UUID.randomUUID().toString());
        } else {
            Optional<Wishlist> existingWishlist = wishlistRepository.findById(wishlist.getWishlistId());
            if (existingWishlist.isPresent()) {
                throw new RuntimeException("Wishlist with ID " + wishlist.getWishlistId() + " already exists.");
            }
        }
        return wishlistRepository.saveAndFlush(wishlist);
    }

    @Override
    public void deleteWishlist(String id) {
        wishlistRepository.deleteById(id);
    }

    @Override
    public Wishlist updateWishlist(Wishlist updatedWishlist) {
        return wishlistRepository.saveAndFlush(updatedWishlist);
    }

}
