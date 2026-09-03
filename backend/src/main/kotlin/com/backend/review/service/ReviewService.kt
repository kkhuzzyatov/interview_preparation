package com.backend.review.service

import com.backend.answer.dto.AnswerResult
import com.backend.answer.entity.Answer
import com.backend.answer.repository.AnswerRepository
import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.exceptions.NoCardsAvailableException
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class ReviewService(
    private val cardRepository: CardRepository,
    private val answerRepository: AnswerRepository,
    private val multiplierService: ReviewMultiplierCalculator,
) {
    fun getNextCard(): Card {
        val cards = cardRepository.findAll()

        if (cards.isEmpty()) {
            throw NoCardsAvailableException()
        }

        val weightedCards =
            cards
                .map { card ->
                    val answers = answerRepository.findByCardIdOrderByCreatedAtDesc(card.id)

                    val weight =
                        calculateWeight(
                            card = card,
                            answers = answers,
                        )

                    WeightedCard(
                        card = card,
                        weight = weight,
                    )
                }.filter { it.weight > 0.0 }

        if (weightedCards.isEmpty()) {
            throw NoCardsAvailableException()
        }

        return selectRandomCard(weightedCards)
    }

    private fun calculateWeight(
        card: Card,
        answers: List<Answer>,
    ): Double {
        val meetChanceMultiplier =
            multiplierService.calculateMeetChanceMultiplier(
                card.meetChance.toDouble(),
            )

        val difficultyMultiplier =
            multiplierService.calculateDifficultyMultiplier(
                answers.map {
                    AnswerResult(
                        score = it.score,
                        createdAt = it.createdAt,
                    )
                },
            )

        val recencyMultiplier =
            answers
                .firstOrNull()
                ?.let {
                    multiplierService.calculateRecencyMultiplier(it.createdAt)
                }
                ?: 1.5

        return meetChanceMultiplier *
            difficultyMultiplier *
            recencyMultiplier
    }

    private fun selectRandomCard(weightedCards: List<WeightedCard>): Card {
        val totalWeight = weightedCards.sumOf { it.weight }

        val randomValue = Random.nextDouble() * totalWeight

        var accumulatedWeight = 0.0

        for (weightedCard in weightedCards) {
            accumulatedWeight += weightedCard.weight

            if (randomValue < accumulatedWeight) {
                return weightedCard.card
            }
        }

        // Protect against floating-point rounding.
        return weightedCards.last().card
    }
}
