package com.ecommerce.wishlist.Service.impl;

import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.exception.WishlistAlreadyExistsException;
import com.ecommerce.wishlist.exception.WishlistNotFoundException;
import com.ecommerce.wishlist.repository.WishlistRepository;
import com.ecommerce.wishlist.service.impl.WishlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WishlistServiceImplTest {
    @Mock
    private WishlistRepository wishlistRepository;
    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<Wishlist> getWishlists() {
        List<Wishlist> wishlists = new ArrayList<>();
        Wishlist wishlist1 = new Wishlist();
        wishlist1.setWishlistId("1");
        wishlist1.setUserId("1");
        wishlist1.setProductId("1");
        wishlists.add(wishlist1);
        Wishlist wishlist2 = new Wishlist();
        wishlist2.setWishlistId("2");
        wishlist2.setUserId("2");
        wishlist2.setProductId("2");
        wishlists.add(wishlist2);
        return wishlists;
    }

    private static Page<Wishlist> getWishlists(Pageable pageable) {
        List<Wishlist> wishlists = getWishlists();
        Page<Wishlist> expectedWishlists = new PageImpl<>(wishlists, pageable, wishlists.size());
        return expectedWishlists;
    }

    private static Wishlist getWishlist() {
        Wishlist wishlist = new Wishlist();
        wishlist.setWishlistId("1");
        wishlist.setUserId("1");
        wishlist.setProductId("1");
        return wishlist;
    }

    @Test
    void testGetWishlists() {
        // Prepare test data
        Pageable pageable = mock(Pageable.class);
        Page<Wishlist> expectedWishlists = getWishlists(pageable);
        when(wishlistRepository.findAll(pageable)).thenReturn(expectedWishlists);
        // Call the method to be tested
        Page<Wishlist> actualWishlists = wishlistService.getWishlists(pageable);

        // Verify the results
        assertEquals(expectedWishlists, actualWishlists);
        verify(wishlistRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetWishlistById_ExistingId() {
        // Prepare test data
        String wishlistId = "1";
        Wishlist expectedWishlist = getWishlist();
        when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.of(expectedWishlist));
        // Call the method to be tested
        Wishlist actualWishlist = wishlistService.getWishlistById(wishlistId);
        // Verify the results
        assertEquals(expectedWishlist, actualWishlist);
        verify(wishlistRepository, times(1)).findById(wishlistId);
    }

    @Test
    void testGetWishlistById_NonExistingId() {
        // Prepare test data
        String wishlistId = "1";
        when(wishlistRepository.findById(wishlistId)).thenReturn(Optional.empty());
        // Call the method to be tested
        assertThrows(WishlistNotFoundException.class, () -> wishlistService.getWishlistById(wishlistId));
        verify(wishlistRepository, times(1)).findById(wishlistId);
    }

    @Test
    void testCreateWishlist_Successful() {
        Wishlist wishlist = getWishlist();
        when(wishlistRepository.saveAndFlush(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Wishlist createdWishlist = wishlistService.createWishlist(wishlist);
        assertNotNull(createdWishlist.getWishlistId());
        verify(wishlistRepository, times(1)).saveAndFlush(wishlist);
    }

    @Test
    void testCreateWishlistAlreadyExists() {
        Wishlist wishlist = getWishlist();
        when(wishlistRepository.findById(wishlist.getWishlistId())).thenReturn(Optional.of(wishlist));
        assertThrows(WishlistAlreadyExistsException.class, () -> wishlistService.createWishlist(wishlist));
        verify(wishlistRepository, times(1)).findById(wishlist.getWishlistId());
    }

    @Test
    void testDeleteWishlist_ExistingWishlist() {
        String wishlistId = "1";
        when(wishlistRepository.existsById(wishlistId)).thenReturn(true);
        doNothing().when(wishlistRepository).deleteById(wishlistId);
        wishlistService.deleteWishlist(wishlistId);
        verify(wishlistRepository, times(1)).existsById(wishlistId);
        verify(wishlistRepository, times(1)).deleteById(wishlistId);
    }

    @Test
    void testDeleteWishlist_NonExistingWishlist() {
        String wishlistId = "1";
        when(wishlistRepository.existsById(wishlistId)).thenReturn(false);
        assertThrows(WishlistNotFoundException.class, () -> wishlistService.deleteWishlist(wishlistId));
        verify(wishlistRepository, times(1)).existsById(wishlistId);
    }

    @Test
    void testUpdateWishlist_ExistingWishlist() {
        String wishlistId = "1";
        Wishlist updatedWishlist = getUpdatedWishlist();
        updatedWishlist.setWishlistId(wishlistId);
        when(wishlistRepository.existsById(wishlistId)).thenReturn(true);
        when(wishlistRepository.saveAndFlush(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Wishlist result = wishlistService.updateWishlist(updatedWishlist);
        assertNotNull(result.getWishlistId());
        verify(wishlistRepository, times(1)).existsById(wishlistId);
        verify(wishlistRepository, times(1)).saveAndFlush(updatedWishlist);
    }

    @Test
    void testUpdateWishlist_NonExistingWishlist() {
        String wishlistId = "1";
        Wishlist updatedWishlist = getUpdatedWishlist();
        updatedWishlist.setWishlistId(wishlistId);
        when(wishlistRepository.existsById(wishlistId)).thenReturn(false);
        assertThrows(WishlistNotFoundException.class, () -> wishlistService.updateWishlist(updatedWishlist));
        verify(wishlistRepository, times(1)).existsById(wishlistId);
    }

    private static Wishlist getUpdatedWishlist()
    {
        Wishlist updatedWishlist = new Wishlist();
        updatedWishlist.setWishlistId("1");
        updatedWishlist.setUserId("1");
        updatedWishlist.setProductId("1");
        return updatedWishlist;
    }

   /* @Test
    void testPatchWishlist_ExistingWishlist() {
        String wishlistId = "1";
        Wishlist updatedWishlist = getUpdatedWishlist();
        updatedWishlist.setWishlistId(wishlistId);
        when(wishlistRepository.existsById(wishlistId)).thenReturn(true);
        when(wishlistRepository.saveAndFlush(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Wishlist result = wishlistService.updateWishlist(updatedWishlist);
        assertNotNull(result.getWishlistId());
        verify(wishlistRepository, times(1)).existsById(wishlistId);
        verify(wishlistRepository, times(1)).saveAndFlush(updatedWishlist);
    }

    @Test
    void testPatchWishlist_NonExistingWishlist() {
        String wishlistId = "1";
        Wishlist updatedWishlist = getUpdatedWishlist();
        updatedWishlist.setWishlistId(wishlistId);
        when(wishlistRepository.existsById(wishlistId)).thenReturn(false);
        assertThrows(WishlistNotFoundException.class, () -> wishlistService.updateWishlist(updatedWishlist));
        verify(wishlistRepository, times(1)).existsById(wishlistId);
    }*/
}
