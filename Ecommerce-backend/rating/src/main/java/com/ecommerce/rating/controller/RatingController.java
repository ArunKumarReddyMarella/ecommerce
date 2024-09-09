package com.ecommerce.rating.controller;

import com.ecommerce.rating.entity.Rating;
import com.ecommerce.rating.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public ResponseEntity<Page<Rating>> getRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Rating> ratings = ratingService.getRatings(pageable);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{ratingId}")
    public ResponseEntity<Rating> getRatingById(@PathVariable String ratingId) {
        Rating rating = ratingService.getRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody @Valid Rating rating) {
        Rating createdRating = ratingService.createRating(rating);
        return ResponseEntity.ok(createdRating);
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<Rating> updateRating(@PathVariable String ratingId, @RequestBody @Valid Rating rating) {
        rating.setRatingId(ratingId);
        Rating updatedRating = ratingService.updateRating(rating);
        if (updatedRating == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedRating);
    }

    @PatchMapping("/{ratingId}")
    public ResponseEntity<Rating> patchRating(@PathVariable String ratingId, @RequestBody Map<String, Object> updates) {
        Rating existingRating = ratingService.getRatingById(ratingId);
        if (existingRating == null) {
            return ResponseEntity.notFound().build();
        }
        ratingService.patchRating(ratingId, updates);
        Rating updatedRating = ratingService.getRatingById(ratingId);
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<String> deleteRating(@PathVariable String ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.ok("Rating deleted successfully!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
