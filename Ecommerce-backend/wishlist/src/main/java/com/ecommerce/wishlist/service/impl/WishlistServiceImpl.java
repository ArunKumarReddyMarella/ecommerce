package com.ecommerce.wishlist.service.impl;

import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.exception.WishlistAlreadyExistsException;
import com.ecommerce.wishlist.exception.WishlistNotFoundException;
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
        Wishlist optionalWishlist = wishlistRepository.findById(id).orElseThrow(() -> new WishlistNotFoundException("Wishlist not found with ID: " + id));
        return optionalWishlist;
    }

    @Override
    public Wishlist createWishlist(Wishlist wishlist) {
        if (wishlist.getWishlistId() == null) {
            wishlist.setWishlistId(UUID.randomUUID().toString());
        } else {
            Optional<Wishlist> existingWishlist = wishlistRepository.findById(wishlist.getWishlistId());
            if (existingWishlist.isPresent()) {
                throw new WishlistAlreadyExistsException("Wishlist with ID " + wishlist.getWishlistId() + " already exists.");
            }
        }
        return wishlistRepository.saveAndFlush(wishlist);
    }

    @Override
    public void deleteWishlist(String id) {
        if(!wishlistRepository.existsById(id)) {
            throw new WishlistNotFoundException("Wishlist not found with ID: " + id);
        }
        wishlistRepository.deleteById(id);
    }

    @Override
    public Wishlist updateWishlist(Wishlist updatedWishlist) {
        if(!wishlistRepository.existsById(updatedWishlist.getWishlistId())) {
            throw new WishlistNotFoundException("Wishlist not found with ID: " + updatedWishlist.getWishlistId());
        }
        return wishlistRepository.saveAndFlush(updatedWishlist);
    }

    @Override
    public Page<Wishlist> getWishlistByUserId(String userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable);
    }

}
