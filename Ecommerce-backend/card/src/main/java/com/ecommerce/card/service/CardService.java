package com.ecommerce.card.service;

import com.ecommerce.card.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CardService {

    Page<Card> getCards(Pageable pageable);

    Card getCardById(String id);

    Card createCard(Card card);

    void deleteCard(String id);

    Card updateCard(Card updatedCard);

    Card patchCard(String cardId, Map<String, Object> updates);

    Card getCardByCardNumber(String cardNumber);
}
