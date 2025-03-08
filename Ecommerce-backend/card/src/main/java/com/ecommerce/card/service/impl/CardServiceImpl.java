package com.ecommerce.card.service.impl;

import com.ecommerce.card.entity.Card;
import com.ecommerce.card.exception.CardAlreadyExistsException;
import com.ecommerce.card.exception.CardNotFondException;
import com.ecommerce.card.repository.CardRepository;
import com.ecommerce.card.service.CardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFondException("Card not found with id: " + id));
    }

    @Override
    public Card createCard(Card card) {
        if (card.getCardId() == null) {
            card.setCardId(UUID.randomUUID().toString());
        } else {
            Optional<Card> existingCard = cardRepository.findById(card.getCardId());
            if (existingCard.isPresent()) {
                throw new CardAlreadyExistsException("Card with ID " + card.getCardId() + " already exists.");
            }
        }
        return cardRepository.saveAndFlush(card);
    }

    @Override
    public void deleteCard(String id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFondException("Card not found with id: " + id);
        }
        cardRepository.deleteById(id);
    }

    @Override
    public Card updateCard(Card updatedCard) {
        if(!cardRepository.existsById(updatedCard.getCardId())) {
            throw new CardNotFondException("Card not found with id: " + updatedCard.getCardId());
        }
        return cardRepository.saveAndFlush(updatedCard);
    }

    @Override
    public Card patchCard(String cardId, Map<String, Object> updates) {
        Card existingCard = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFondException("Card not found with id: " + cardId));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // for Timestamp support

        try {
            // assuming updates is a Map<String, Object>
            mapper.updateValue(existingCard, updates);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid update field: " + updates);
        }


        cardRepository.save(existingCard);
        return existingCard;
    }

    @Override
    public Card getCardByCardNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new CardNotFondException("Card not found with cardNumber: " + cardNumber));
    }

    @Override
    public List<Card> getCardByUserId(String userId) {
        List<Card> cards = cardRepository.findByUserId(userId);
        if (cards.isEmpty()) {
            throw new CardNotFondException("Cards not found with userId: " + userId);
        }
        return cards;
    }
}
