package com.ecommerce.card.service.impl;

import com.ecommerce.card.entity.Card;
import com.ecommerce.card.repository.CardRepository;
import com.ecommerce.card.service.CardService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Override
    public Page<Card> getCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Override
    public Card getCardById(String id) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        return optionalCard.orElse(null);
    }

    @Override
    public Card createCard(Card card) {
        if (card.getCardId() == null) {
            card.setCardId(UUID.randomUUID().toString());
        } else {
            Optional<Card> existingCard = cardRepository.findById(card.getCardId());
            if (existingCard.isPresent()) {
                throw new RuntimeException("Card with ID " + card.getCardId() + " already exists.");
            }
        }
        return cardRepository.saveAndFlush(card);
    }

    @Override
    public void deleteCard(String id) {
        cardRepository.deleteById(id);
    }

    @Override
    public Card updateCard(Card updatedCard) {
        Card card = cardRepository.saveAndFlush(updatedCard);
        return card;
    }

    @Override
    public void patchCard(String cardId, Map<String, Object> updates) {
        Card existingCard = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = Card.class.getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType() == Timestamp.class) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse((String) value);
                        Timestamp timestampValue = Timestamp.from(odt.toInstant());
                        field.set(existingCard, timestampValue);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " TimeStamp field");
                    }
                } else if (field.getType() == Date.class) {
                    try{
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        java.util.Date utilDate = dateFormat.parse((String) value);
                        field.set(existingCard, new java.sql.Date(utilDate.getTime()));
                    } catch ( ParseException e) {
                        throw new IllegalArgumentException("Invalid format for " + key + " Date field");
                    }
                } else {
                    field.set(existingCard, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid update field: " + key);
            }
        });

        cardRepository.save(existingCard);
    }

    @Override
    public Card getCardByCardNumber(String cardNumber) {
        Optional<Card> optionalCard = cardRepository.findByCardNumber(cardNumber);
        if(optionalCard.isEmpty())
            return null;
        return optionalCard.get();
    }
}
