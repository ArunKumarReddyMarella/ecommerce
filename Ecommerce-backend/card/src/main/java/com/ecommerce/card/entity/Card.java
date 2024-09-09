package com.ecommerce.card.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "card")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @Column(name = "card_id", length = 50)
    private String cardId;

    @Column(name = "card_number", length = 50, nullable = false)
    private String cardNumber;

    @Column(name = "card_holder_name", length = 255, nullable = false)
    private String cardHolderName;

    @Column(name = "card_type", length = 50, nullable = false)
    private String cardType;

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @Column(name = "cvv", nullable = false)
    private Integer cvv;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "created_at")
    private Timestamp createdAt;
}

