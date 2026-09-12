package com.backend.card.service

import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CardService(
    private val cardRepository: CardRepository,
) {
    fun updateCard(
        cardId: UUID,
        question: String,
        answer: String,
    ): Card {
        val existingCard =
            cardRepository
                .findById(cardId)
                .orElseThrow {
                    NoSuchElementException("Card not found")
                }

        existingCard.question = question
        existingCard.answer = answer

        return cardRepository.save(existingCard)
    }
}
