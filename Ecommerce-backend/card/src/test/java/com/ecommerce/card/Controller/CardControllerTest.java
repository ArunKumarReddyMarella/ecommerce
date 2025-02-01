package com.ecommerce.card.Controller;

import com.ecommerce.card.controller.CardController;
import com.ecommerce.card.entity.Card;
import com.ecommerce.card.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private CardController cardController;

    @Test
    void testGetCardsDesc() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, "cardHolderName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Card> expectedCards = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(cardService.getCards(pageable)).thenReturn(expectedCards);

        ResponseEntity<Page<Card>> response = cardController.getCards(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCards, response.getBody());
        for (int i = 0; i < expectedCards.getContent().size(); i++) {
            assertCardFields(expectedCards.getContent().get(i), response.getBody().getContent().get(i));
        }
    }
    @Test
    void testGetCardsAsc() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(Sort.Direction.ASC, "cardHolderName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Card> expectedCards = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(cardService.getCards(pageable)).thenReturn(expectedCards);

        ResponseEntity<Page<Card>> response = cardController.getCards(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCards, response.getBody());
        for (int i = 0; i < expectedCards.getContent().size(); i++) {
            assertCardFields(expectedCards.getContent().get(i), response.getBody().getContent().get(i));
        }
    }

    private static Card getCard() {
        Card card = new Card();
        card.setCardId("1");
        card.setCardNumber("1234567890123456");
        card.setCardHolderName("John Doe");
        card.setCardType("Visa");
        card.setExpirationDate(new java.sql.Date(System.currentTimeMillis()));
        card.setCvv(123);
        card.setUserId("user1");
        card.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        return card;
    }

    @Test
    void testGetCardById() {
        String cardId = "1";
        Card expectedCard = getCard();
        when(cardService.getCardById(cardId)).thenReturn(expectedCard);
        ResponseEntity<Card> response = cardController.getCardById(cardId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCard, response.getBody());
        assertCardFields(expectedCard, Objects.requireNonNull(response.getBody()));
        verify(cardService, times(1)).getCardById(cardId);
    }

    @Test
    void testGetCardByNumber() {
        String cardNumber = "1234567890123456";
        Card expectedCard = getCard();
        when(cardService.getCardByCardNumber(cardNumber)).thenReturn(expectedCard);
        ResponseEntity<Card> response = cardController.getCardByNumber(cardNumber);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCard, response.getBody());
        assertCardFields(expectedCard, Objects.requireNonNull(response.getBody()));
        verify(cardService, times(1)).getCardByCardNumber(cardNumber);
    }

    @Test
    void testCreateCard() {
        Card card = getCard();
        when(cardService.createCard(card)).thenReturn(card);
        ResponseEntity<Card> response = cardController.createCard(card);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(card, response.getBody());
        assertCardFields(card, Objects.requireNonNull(response.getBody()));
        verify(cardService, times(1)).createCard(card);

    }

    @Test
    void testUpdateCard() {
        String cardId = "1";
        Card card = getCard();
        when(cardService.updateCard(card)).thenReturn(card);
        ResponseEntity<Card> response = cardController.updateCard(cardId, card);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(card, response.getBody());
        assertCardFields(card, Objects.requireNonNull(response.getBody()));
        verify(cardService, times(1)).updateCard(card);
    }

    @Test
    void testUpdateCard_NotFound() {
        String cardId = "1";
        Card card = getCard();
        when(cardService.updateCard(card)).thenReturn(null);
        ResponseEntity<Card> response = cardController.updateCard(cardId, card);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(cardService, times(1)).updateCard(card);
    }

    @Test
    void testPatchCard() {
        String cardId = "1";
        Card card = getCard();
        Map<String, Object> updates = Map.of("cardHolderName", "Updated Name");
        when(cardService.getCardById(cardId)).thenReturn(card);
        when(cardService.patchCard(cardId, updates)).thenReturn(card);
        ResponseEntity<Card> response = cardController.patchCard(cardId, updates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(card, response.getBody());
        assertCardFields(card, Objects.requireNonNull(response.getBody()));
        verify(cardService, times(1)).getCardById(cardId);
        verify(cardService, times(1)).patchCard(cardId, updates);
    }

    @Test
    void testPatchCard_NotFound() {
        String cardId = "1";
        Card card = getCard();
        Map<String, Object> updates = Map.of("cardHolderName", "Updated Name");
        when(cardService.getCardById(cardId)).thenReturn(null);
        ResponseEntity<Card> response = cardController.patchCard(cardId, updates);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(cardService, times(1)).getCardById(cardId);
        verify(cardService, never()).patchCard(cardId, updates);
    }

    @Test
    void testDeleteCard() {
        String cardId = UUID.randomUUID().toString();
        ResponseEntity<String> response = cardController.deleteCard(cardId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card deleted successfully!.", response.getBody());
        verify(cardService, times(1)).deleteCard(cardId);
    }

    private void assertCardFields(Card actualCard, Card expectedCard) {
        assertEquals(actualCard.getCardId(),expectedCard.getCardId());
        assertEquals(actualCard.getCardNumber(),expectedCard.getCardNumber());
        assertEquals(actualCard.getCardHolderName(),expectedCard.getCardHolderName());
        assertEquals(actualCard.getCardType(),expectedCard.getCardType());
        assertEquals(actualCard.getCvv(),expectedCard.getCvv());
        assertEquals(actualCard.getUserId(),expectedCard.getUserId());
        assertEquals(actualCard.getCreatedAt(),expectedCard.getCreatedAt());
    }
}
