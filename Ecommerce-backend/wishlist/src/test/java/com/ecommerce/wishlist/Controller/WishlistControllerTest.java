package com.ecommerce.wishlist.Controller;

import com.ecommerce.wishlist.controller.WishlistController;
import com.ecommerce.wishlist.entity.Wishlist;
import com.ecommerce.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WishlistControllerTest {
    @Mock
    private WishlistService wishlistService;
    @InjectMocks
    private WishlistController wishlistController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWishlistsDesc()
    {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Wishlist> expectedWishlists = getWishlists(pageable);
        when(wishlistService.getWishlists(pageable)).thenReturn(expectedWishlists);

        ResponseEntity<Page<Wishlist>> response = wishlistController.getWishlists(page, size, sortDirection);

        assertEquals(expectedWishlists, response.getBody());
        verify(wishlistService, times(1)).getWishlists(pageable);
    }

    @Test
    void testGetWishlistsAsc()
    {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Wishlist> expectedWishlists = getWishlists(pageable);
        when(wishlistService.getWishlists(pageable)).thenReturn(expectedWishlists);

        ResponseEntity<Page<Wishlist>> response = wishlistController.getWishlists(page, size, sortDirection);

        assertEquals(expectedWishlists, response.getBody());
        verify(wishlistService, times(1)).getWishlists(pageable);
    }

    private static Page<Wishlist> getWishlists(Pageable pageable) {
        List<Wishlist> wishlists=new ArrayList<>();
        Wishlist wishlist1=new Wishlist();
        wishlist1.setWishlistId("1");
        wishlist1.setUserId("1");
        wishlist1.setProductId("1");
        wishlists.add(wishlist1);
        Wishlist wishlist2=new Wishlist();
        wishlist2.setWishlistId("2");
        wishlist2.setUserId("2");
        wishlist2.setProductId("2");
        wishlists.add(wishlist2);
        Page<Wishlist> wishlistsPage = new PageImpl<Wishlist>(wishlists, pageable, wishlists.size());
        return wishlistsPage;
    }

    private static Wishlist getWishlist() {
        Wishlist wishlist = new Wishlist();
        wishlist.setWishlistId("1");
        wishlist.setUserId("1");
        wishlist.setProductId("1");
        return wishlist;
    }

    @Test
    void testGetWishlistById() {
        String id = "1";
        Wishlist wishlist = getWishlist();
        when(wishlistService.getWishlistById(id)).thenReturn(wishlist);
        ResponseEntity<Wishlist> response = wishlistController.getWishlistById(id);
        assertEquals(wishlist, response.getBody());
        verify(wishlistService, times(1)).getWishlistById(id);
    }

    @Test
    void testCreateWishlist() {
        Wishlist wishlist = getWishlist();
        when(wishlistService.createWishlist(wishlist)).thenReturn(wishlist);
        ResponseEntity<Wishlist> response = wishlistController.createWishlist(wishlist);
        assertEquals(wishlist, response.getBody());
        verify(wishlistService, times(1)).createWishlist(wishlist);
    }

    @Test
    void testUpdateWishlist() {
        String wishlistId = "1";
        Wishlist wishlist = getWishlist();
        when(wishlistService.updateWishlist(wishlist)).thenReturn(wishlist);
        ResponseEntity<Wishlist> response = wishlistController.updateWishlist(wishlistId, wishlist);
        assertEquals(wishlist, response.getBody());
        verify(wishlistService, times(1)).updateWishlist(wishlist);
    }

    @Test
    void testDeleteWishlist() {
        String wishlistId = "1";
        wishlistController.deleteWishlist(wishlistId);
        verify(wishlistService, times(1)).deleteWishlist(wishlistId);
    }
}
