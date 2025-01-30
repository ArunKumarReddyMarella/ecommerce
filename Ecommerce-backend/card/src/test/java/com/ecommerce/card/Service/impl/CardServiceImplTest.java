package com.ecommerce.card.Service.impl;

import com.ecommerce.card.entity.Card;
import com.ecommerce.card.exception.CardNotFondException;
import com.ecommerce.card.repository.CardRepository;
import com.ecommerce.card.service.CardService;
import com.ecommerce.card.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CardServiceImplTest {
    @Mock
    CardRepository cardRepository;
    @InjectMocks
    CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static List<Card> getCards() {
        List<Card> cardslist=new ArrayList<>();
        Card card1=new Card();
        card1.setCardId("1");
        card1.setCardNumber("1234567890123456");
        card1.setCardHolderName("John Doe");
        card1.setCardType("Visa");
        card1.setExpirationDate(new java.sql.Date(System.currentTimeMillis()));
        card1.setCvv(123);
        card1.setUserId("user1");
        card1.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        cardslist.add(card1);
        Card card2=new Card();
        card2.setCardId("2");
        card2.setCardNumber("1234567890123457");
        card2.setCardHolderName("John Doe");
        card2.setCardType("Visa");
        card2.setExpirationDate(new java.sql.Date(System.currentTimeMillis()));
        card2.setCvv(123);
        card2.setUserId("user1");
        card2.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        cardslist.add(card2);
        return cardslist;
    }

    private static Page<Card> getCards(Pageable pageable)
    {
        List<Card> cardslist=getCards();
        Page<Card> expectedCards=new PageImpl<>(cardslist,pageable,cardslist.size());
        return expectedCards;
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
    void testGetCards() {
        Pageable pageable = mock(Pageable.class);
        Page<Card> expectedCards = getCards(pageable);
        when(cardRepository.findAll(pageable)).thenReturn(expectedCards);
        Page<Card> actualCards = cardService.getCards(pageable);
        assertEquals(expectedCards, actualCards);
        verify(cardRepository, times(1)).findAll(pageable);
    }
    @Test
    void testGetCardById_ExistingId() {
        String cardId = "1";
        Card expectedCard = getCard();
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(expectedCard));
        Card actualCard = cardService.getCardById(cardId);
        assertEquals(expectedCard, actualCard);
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    void testGetCardById_NonexistentId() {
        String cardId = "1";
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());
        assertThrows(CardNotFondException.class, () -> cardService.getCardById(cardId));
        verify(cardRepository, times(1)).findById(cardId);
    }

    @Test
    void testCreateCard_Success() {
        Card card = getCard();
        card.setCardId(null);
        when(cardRepository.saveAndFlush(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Card createdCard = cardService.createCard(card);
        assertNotNull(createdCard.getCardId());
        verify(cardRepository, times(1)).saveAndFlush(any(Card.class));
    }

    @Test
    void testCreateNewCardSuccess() {
        Card card = getCard();
        card.setCardId(null);
        when(cardRepository.saveAndFlush(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Card createdCard = cardService.createCard(card);
        assertNotNull(createdCard.getCardId());
        verify(cardRepository, times(1)).saveAndFlush(any(Card.class));
    }

    @Test
    void testUpdateCard_success() {
        String cardId = "1";
        Card updatedCard = getCard();
        updatedCard.setCardId(cardId);
        when(cardRepository.existsById(cardId)).thenReturn(true);
        when(cardRepository.saveAndFlush(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Card savedCard = cardService.updateCard(updatedCard);
        assertEquals(cardId, savedCard.getCardId());
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardRepository, times(1)).saveAndFlush(any(Card.class));
    }

    @Test
    void testUpdateCard_failure() {
        String cardId = "1";
        Card updatedCard = getCard();
        updatedCard.setCardId(cardId);
        when(cardRepository.existsById(cardId)).thenReturn(false);
        assertThrows(CardNotFondException.class, () -> cardService.updateCard(updatedCard));
        verify(cardRepository, times(1)).existsById(cardId);
    }

    @Test
    void testPatchCard_Success() {
        String cardId = "1";
        Card existingCard = getCard();
        Map<String, Object> updates = Map.of("cardHolderName", "Updated Name");
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Card patchedCard = cardService.patchCard(cardId, updates);
        assertEquals("Updated Name", patchedCard.getCardHolderName());
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardRepository, times(1)).save(existingCard);
    }
    @Test
    void testPatchCard_Failure() {
        String cardId = "1";
        Card existingCard = getCard();
        Map<String, Object> updates = Map.of("cardHolderName", "Updated Name");
        when(cardRepository.existsById(cardId)).thenReturn(false);
        assertThrows(CardNotFondException.class, () -> cardService.patchCard(cardId, updates));
        verify(cardRepository, times(1)).findById(cardId);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void testGetCardByCardNumber_ExistingCard() {
        String cardNumber = "1234567890123456";
        Card expectedCard = getCard();
        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(expectedCard));
        Card actualCard = cardService.getCardByCardNumber(cardNumber);
        assertEquals(expectedCard, actualCard);
        verify(cardRepository, times(1)).findByCardNumber(cardNumber);
    }

    @Test
    void testGetCardByCardNumber_NonexistentCard() {
        String cardNumber = "1234567890123456";
        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());
        assertThrows(CardNotFondException.class, () -> cardService.getCardByCardNumber(cardNumber));
        verify(cardRepository, times(1)).findByCardNumber(cardNumber);
    }

    @Test
    void testDeleteCard_Success() {
        String cardId = "1";
        when(cardRepository.existsById(cardId)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(cardId);
        cardService.deleteCard(cardId);
        verify(cardRepository, times(1)).existsById(cardId);
        verify(cardRepository, times(1)).deleteById(cardId);
    }

    @Test
    void testDeleteCard_NonexistentCard() {
        String cardId = "1";
        when(cardRepository.existsById(cardId)).thenReturn(false);
        assertThrows(CardNotFondException.class, () -> cardService.deleteCard(cardId));
        verify(cardRepository, times(1)).existsById(cardId);
    }
}
