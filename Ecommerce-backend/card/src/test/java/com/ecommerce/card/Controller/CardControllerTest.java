package com.ecommerce.card.Controller;

import com.ecommerce.card.controller.CardController;
import com.ecommerce.card.entity.Card;
import com.ecommerce.card.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CardControllerTest {
    @Mock
    private CardService cardService;
    @InjectMocks
    private CardController cardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetCardsDesc() {
        int page = 0;
        int size = 10;
        String sortDirection = "desc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "cardHolderName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Card> expectedCards = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(cardService.getCards(pageable)).thenReturn(expectedCards);

        ResponseEntity<Page<Card>> response = cardController.getCards(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCards, response.getBody());

    }
    @Test
    void testGetCardsAsc() {
        int page = 0;
        int size = 10;
        String sortDirection = "asc";
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "cardHolderName");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Card> expectedCards = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(cardService.getCards(pageable)).thenReturn(expectedCards);

        ResponseEntity<Page<Card>> response = cardController.getCards(page, size, sortDirection);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCards, response.getBody());
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
    }

    @Test
    void testGetCardByNumber() {
        String cardNumber = "1234567890123456";
        Card expectedCard = getCard();
        when(cardService.getCardByCardNumber(cardNumber)).thenReturn(expectedCard);
        ResponseEntity<Card> response = cardController.getCardByNumber(cardNumber);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCard, response.getBody());
    }

    @Test
    void testCreateCard() {
        Card card = getCard();
        when(cardService.createCard(card)).thenReturn(card);
        ResponseEntity<Card> response = cardController.createCard(card);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(card, response.getBody());
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
        verify(cardService, times(1)).updateCard(card);
    }

    @Test
    void testDeleteCard() {
        String cardId = UUID.randomUUID().toString();
        ResponseEntity<String> response = cardController.deleteCard(cardId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Card deleted successfully!.", response.getBody());
        verify(cardService, times(1)).deleteCard(cardId);
    }
}
