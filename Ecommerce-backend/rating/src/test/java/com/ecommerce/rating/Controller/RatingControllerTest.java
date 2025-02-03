package com.ecommerce.rating.Controller;

import com.ecommerce.rating.controller.RatingController;
import com.ecommerce.rating.entity.Rating;
import com.ecommerce.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RatingControllerTest {
    @Mock
    private RatingService ratingService;
    @InjectMocks
    private RatingController ratingController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRatingsDescending() {
        int page=0;
        int size=10;
        String sortDirection="desc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Rating> expectedRatings = getRatings(pageable);
        when(ratingService.getRatings(pageable)).thenReturn(expectedRatings);
        ResponseEntity<Page<Rating>> response = ratingController.getRatings(page, size, sortDirection);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRatings, response.getBody());
        verify(ratingService, times(1)).getRatings(pageable);
    }

    @Test
    void testGetRatingsAscending() {
        int page=0;
        int size=10;
        String sortDirection="asc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Rating> expectedRatings = getRatings(pageable);
        when(ratingService.getRatings(pageable)).thenReturn(expectedRatings);
        ResponseEntity<Page<Rating>> response = ratingController.getRatings(page, size, sortDirection);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRatings, response.getBody());
        verify(ratingService, times(1)).getRatings(pageable);
    }

    private static Page<Rating> getRatings(Pageable pageable) {
        List<Rating> ratings = new ArrayList<>();
        Rating rating = new Rating();
        rating.setRatingId("1");
        rating.setProductId("1");
        rating.setUserId("1");
        rating.setRating(5);
        rating.setReview("Good");
        ratings.add(rating);
        Rating rating1 = new Rating();
        rating1.setRatingId("2");
        rating1.setProductId("2");
        rating1.setUserId("2");
        rating1.setRating(4);
        rating1.setReview("Average");
        ratings.add(rating1);
        Page<Rating> expectedRatings = new PageImpl<>(ratings, pageable, ratings.size());
        return expectedRatings;
    }

    private static Rating getRating() {
        Rating rating = new Rating();
        rating.setRatingId("1");
        rating.setProductId("1");
        rating.setUserId("1");
        rating.setRating(5);
        rating.setReview("Good");
        return rating;
    }

    @Test
    void testGetRatingById() {
        Rating rating = getRating();
        when(ratingService.getRatingById(rating.getRatingId())).thenReturn(rating);
        ResponseEntity<Rating> response = ratingController.getRatingById(rating.getRatingId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rating, response.getBody());
        verify(ratingService, times(1)).getRatingById(rating.getRatingId());
    }

    @Test
    void testCreateRating() {
        Rating rating = getRating();
        when(ratingService.createRating(rating)).thenReturn(rating);
        ResponseEntity<Rating> response = ratingController.createRating(rating);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rating, response.getBody());
        verify(ratingService, times(1)).createRating(rating);
    }
    @Test
    void testUpdateRating() {
        Rating rating = getRating();
        when(ratingService.updateRating(rating)).thenReturn(rating);
        ResponseEntity<Rating> response = ratingController.updateRating(rating.getRatingId(), rating);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rating, response.getBody());
        verify(ratingService, times(1)).updateRating(rating);
    }
    @Test
    void testDeleteRating() {
        String ratingId = "1";
        ResponseEntity<String> response = ratingController.deleteRating(ratingId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rating deleted successfully!", response.getBody());
        verify(ratingService, times(1)).deleteRating(ratingId);
    }
   /* @Test
    void testPatchRating() {
        String ratingId = "1";
        Map<String, Object> updates = Map.of("rating", 4);
        Rating updatedRating = getRating();
        when(ratingService.patchRating(ratingId, updates)).thenReturn(updatedRating);
        ResponseEntity<Rating> response = ratingController.patchRating(ratingId, updates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRating, response.getBody());
        verify(ratingService, times(1)).patchRating(ratingId, updates);
    }*/
}
