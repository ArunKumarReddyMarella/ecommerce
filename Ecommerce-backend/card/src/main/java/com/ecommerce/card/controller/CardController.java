package com.ecommerce.card.controller;

import com.ecommerce.card.entity.Card;
import com.ecommerce.card.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<Page<Card>> getCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "cardHolderName"); // Example: sorting by cardHolderName
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Card> cards = cardService.getCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable String id){
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("cardNumber") // Assuming you want to fetch by card number
    public ResponseEntity<Card> getCardByNumber(@RequestParam("paramName") String cardNumber){
        Card card = cardService.getCardByCardNumber(cardNumber);
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card){
        Card createdCard = cardService.createCard(card);
        return ResponseEntity.ok(createdCard);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(@PathVariable String cardId, @RequestBody Card card) {
        Card updatedCard = cardService.updateCard(card);
        if (updatedCard == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCard);
    }

    @PatchMapping("/{cardId}")
    public ResponseEntity<Card> patchCard(@PathVariable String cardId, @RequestBody Map<String, Object> updates) {
        Card existingCard = cardService.getCardById(cardId);
        if (existingCard == null) {
            return ResponseEntity.notFound().build();
        }
        cardService.patchCard(cardId, updates);
        Card updatedCard = cardService.getCardById(cardId);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCard(@PathVariable String id){
        cardService.deleteCard(id);
        return ResponseEntity.ok("Card deleted successfully!.");
    }
}
