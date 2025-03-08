package com.ecommerce.rating.service.impl;

import com.ecommerce.rating.entity.Rating;
import com.ecommerce.rating.exception.RatingAlreadyExistsException;
import com.ecommerce.rating.exception.RatingNotFoundException;
import com.ecommerce.rating.repository.RatingRepository;
import com.ecommerce.rating.service.RatingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public Page<Rating> getRatings(Pageable pageable) {
        return ratingRepository.findAll(pageable);
    }

    @Override
    public Rating getRatingById(String ratingId) {
        return ratingRepository.findById(ratingId).orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + ratingId));
    }

    @Override
    public Rating createRating(Rating rating) {
        if (rating.getRatingId() == null)
            rating.setRatingId(UUID.randomUUID().toString());
        else {
            Optional<Rating> existingRating = ratingRepository.findById(rating.getRatingId());
            if (existingRating.isPresent()) {
                throw new RatingAlreadyExistsException("Rating with ID " + rating.getRatingId() + " already exists.");
            }
        }
        return ratingRepository.saveAndFlush(rating);
    }

    @Override
    public Rating updateRating(Rating updatedRating) {
        if(!ratingRepository.existsById(updatedRating.getRatingId()))
            throw new RatingNotFoundException("Rating not found with ID: " + updatedRating.getRatingId());
        return ratingRepository.saveAndFlush(updatedRating);
    }

    @Override
    public void patchRating(String ratingId, Map<String, Object> updates) {
        Rating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with ID: " + ratingId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingRating, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }
        ratingRepository.save(existingRating);
    }

    @Override
    public void deleteRating(String ratingId) {
        if(!ratingRepository.existsById(ratingId))
            throw new RatingNotFoundException("Rating not found with ID: " + ratingId);
        ratingRepository.deleteById(ratingId);
    }

    @Override
    public Page<Rating> getRatingsByProductId(String productId, Pageable pageable) {
        if(productId == null)
            throw new IllegalArgumentException("Product ID cannot be null");
//        if(!ratingRepository.existsByProductId(productId))
//            throw new RatingNotFoundException("No ratings found for product ID: " + productId);
        return ratingRepository.findByProductId(productId, pageable);
    }
}
