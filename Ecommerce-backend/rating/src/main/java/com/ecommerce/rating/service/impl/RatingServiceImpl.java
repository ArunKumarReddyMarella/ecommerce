package com.ecommerce.rating.service.impl;

import com.ecommerce.rating.entity.Rating;
import com.ecommerce.rating.repository.RatingRepository;
import com.ecommerce.rating.service.RatingService;
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
        Optional<Rating> optionalRating = ratingRepository.findById(ratingId);
        return optionalRating.orElse(null);
    }

    @Override
    public Rating createRating(Rating rating) {
        if (rating.getRatingId() == null)
            rating.setRatingId(UUID.randomUUID().toString());
        else {
            Optional<Rating> existingRating = ratingRepository.findById(rating.getRatingId());
            if (existingRating.isPresent()) {
                throw new RuntimeException("Rating with ID " + rating.getRatingId() + " already exists.");
            }
        }
        return ratingRepository.saveAndFlush(rating);
    }

    @Override
    public Rating updateRating(Rating updatedRating) {
        return ratingRepository.saveAndFlush(updatedRating);
    }

    @Override
    public void patchRating(String ratingId, Map<String, Object> updates) {
        Rating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = Rating.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingRating, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else {
                    field.set(existingRating, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        ratingRepository.save(existingRating);
    }

    @Override
    public void deleteRating(String ratingId) {
        ratingRepository.deleteById(ratingId);
    }
}
