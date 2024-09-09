package com.ecommerce.rating.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rating")
public class Rating {

    @Id
    @NotBlank(message = "Rating ID cannot be blank")
    private String ratingId;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String review;

    @CreationTimestamp
    private Timestamp createdAt;
}
