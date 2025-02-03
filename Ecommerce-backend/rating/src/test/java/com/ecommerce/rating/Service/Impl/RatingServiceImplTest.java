package com.ecommerce.rating.Service.Impl;

import com.ecommerce.rating.entity.Rating;
import com.ecommerce.rating.exception.RatingAlreadyExistsException;
import com.ecommerce.rating.exception.RatingNotFoundException;
import com.ecommerce.rating.repository.RatingRepository;
import com.ecommerce.rating.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RatingServiceImplTest {
    @Mock
    private RatingRepository ratingRepository;
    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<Rating> getRatings() {
        List<Rating> ratings = new ArrayList<Rating>();
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
        return ratings;
    }

    private static Page<Rating> getRatings(Pageable pageable){
        List<Rating> ratings = getRatings();
        Page<Rating> expectedRatings = new PageImpl<>(ratings, pageable, ratings.size());
        return expectedRatings;
    }

    private static Rating getRating(){
        Rating rating = new Rating();
        rating.setRatingId("1");
        rating.setProductId("1");
        rating.setUserId("1");
        rating.setRating(5);
        rating.setReview("Good");
        return rating;
    }

    @Test
    void testGetRatings() {
        Pageable pageable = mock(Pageable.class);
        Page<Rating> expectedRatings = getRatings(pageable);
        when(ratingRepository.findAll(pageable)).thenReturn(expectedRatings);
        Page<Rating> actualRatings = ratingService.getRatings(pageable);
        assertEquals(expectedRatings, actualRatings);
        verify(ratingRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetRatingById_ExistingId() {
        String ratingId = "1";
        Rating rating=getRating();
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        Rating actualRating = ratingService.getRatingById(ratingId);
        assertEquals(rating, actualRating);
        verify(ratingRepository, times(1)).findById(ratingId);
    }

    @Test
    void testGetRatingById_NonExistingId() {
        String ratingId = "1";
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.empty());
        assertThrows(RatingNotFoundException.class, () -> ratingService.getRatingById(ratingId));
        verify(ratingRepository, times(1)).findById(ratingId);
    }

    @Test
    public void testCreateRating_Success() {
        Rating rating = getRating();
        rating.setRatingId(null);
        when(ratingRepository.saveAndFlush(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Rating actualRating = ratingService.createRating(rating);
        assertNotNull(actualRating.getRatingId());
        verify(ratingRepository, times(1)).saveAndFlush(any(Rating.class));
    }

    @Test
    public void testCreateRating_ExistingId() {
        Rating rating = getRating();
        rating.setRatingId("1");
        when(ratingRepository.findById(rating.getRatingId())).thenReturn(Optional.of(rating));
        assertThrows(RatingAlreadyExistsException.class, () -> ratingService.createRating(rating));
        verify(ratingRepository, times(1)).findById(rating.getRatingId());
    }

    @Test
    public void testUpdateRating_Success() {
        Rating rating = getRating();
        when(ratingRepository.existsById(rating.getRatingId())).thenReturn(true);
        when(ratingRepository.saveAndFlush(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Rating actualRating = ratingService.updateRating(rating);
        assertEquals(rating, actualRating);
        verify(ratingRepository, times(1)).existsById(rating.getRatingId());
        verify(ratingRepository, times(1)).saveAndFlush(any(Rating.class));
    }

    @Test
    public void testUpdateRating_NonExistingId() {
        Rating rating = getRating();
        rating.setRatingId("1");
        when(ratingRepository.existsById(rating.getRatingId())).thenReturn(false);
        assertThrows(RatingNotFoundException.class, () -> ratingService.updateRating(rating));
        verify(ratingRepository, times(1)).existsById(rating.getRatingId());
    }

    @Test
    public void testDeleteRating_Success() {
        String ratingId = "1";
        when(ratingRepository.existsById(ratingId)).thenReturn(true);
        ratingService.deleteRating(ratingId);
        verify(ratingRepository, times(1)).existsById(ratingId);
        verify(ratingRepository, times(1)).deleteById(ratingId);
    }

    @Test
    public void testDeleteRating_NonExistingId() {
        String ratingId = "1";
        when(ratingRepository.existsById(ratingId)).thenReturn(false);
        assertThrows(RatingNotFoundException.class, () -> ratingService.deleteRating(ratingId));
        verify(ratingRepository, times(1)).existsById(ratingId);
    }

   /* @Test
    public void testPatchRating_Success() {
        String ratingId = "4";
        Map<String, Object> updates = new HashMap<>();
        updates.put("rating", 4);
        Rating expectedRating = getRating();
        expectedRating.setRating(4);
        when(ratingRepository.existsById(ratingId)).thenReturn(true);
        when(ratingRepository.saveAndFlush(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Rating actualRating = ratingService.patchRating(ratingId, updates);
        assertEquals(expectedRating, actualRating);
        verify(ratingRepository, times(1)).existsById(ratingId);
        verify(ratingRepository, times(1)).saveAndFlush(any(Rating.class));
    }*/

    @Test
    public void testPatchRating_NonExistingId() {
        String ratingId = "4";
        Map<String, Object> updates = new HashMap<>();
        updates.put("rating", 4);
        when(ratingRepository.existsById(ratingId)).thenReturn(false);
        assertThrows(RatingNotFoundException.class, () -> ratingService.patchRating(ratingId, updates));
    }
}
