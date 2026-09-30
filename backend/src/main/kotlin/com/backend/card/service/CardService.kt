package com.backend.card.service

import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.desk.repository.DeskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class CardService(
    private val cardRepository: CardRepository,
    private val deskRepository: DeskRepository,
) {
    @Transactional
    fun createCard(
        question: String,
        answer: String,
        deskId: UUID,
    ): Card {
        val desk =
            deskRepository
                .findById(deskId)
                .orElseThrow {
                    NoSuchElementException("Desk not found")
                }

        val card =
            Card(
                id = UUID.randomUUID(),
                question = question,
                answer = answer,
                desk = desk,
                meetChance = BigDecimal.ZERO,
            )

        return cardRepository.save(card)
    }

    @Transactional
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

    @Transactional
    fun deleteCard(cardId: UUID) {
        val existingCard =
            cardRepository
                .findById(cardId)
                .orElseThrow {
                    NoSuchElementException("Card not found")
                }

        cardRepository.delete(existingCard)
    }
}
