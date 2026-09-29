package com.backend.review.service

import com.backend.answer.dto.AnswerResult
import com.backend.answer.entity.AnswerProcessing
import com.backend.answer.entity.AnswerProcessingStatus
import com.backend.answer.repository.AnswerProcessingRepository
import com.backend.answer.repository.AnswerRepository
import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.exceptions.NoCardsAvailableException
import com.backend.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.security.Principal
import java.time.Clock
import java.time.Instant
import java.util.UUID
import kotlin.random.Random

@Service
class ReviewService(
    private val cardRepository: CardRepository,
    private val answerRepository: AnswerRepository,
    private val answerProcessingRepository: AnswerProcessingRepository,
    private val userRepository: UserRepository,
    private val multiplierService: ReviewMultiplierCalculator,
    private val clock: Clock,
) {
    fun getNextCard(
        principal: Principal,
        deskIds: Collection<UUID>,
    ): ReviewSelection {
        val userId = UUID.fromString(principal.name)

        val user =
            userRepository
                .findById(userId)
                .orElseThrow {
                    IllegalStateException(
                        "Authenticated user $userId does not exist",
                    )
                }

        val cards = cardRepository.findByDeskIdIn(deskIds)

        if (cards.isEmpty()) {
            throw NoCardsAvailableException()
        }

        val weightedCards =
            cards
                .map { card ->
                    calculateWeightedCard(card)
                }.filter { it.weight > 0.0 }

        if (weightedCards.isEmpty()) {
            throw NoCardsAvailableException()
        }

        val selectedCard = selectRandomCard(weightedCards)
        val totalWeight = weightedCards.sumOf { it.weight }

        val selectionProbability =
            calculateSelectionProbability(
                selectedCard = selectedCard,
                totalWeight = totalWeight,
            )

        createAnswerProcessing(
            userId = user.id,
            cardId = selectedCard.card.id,
        )

        return ReviewSelection(
            card = selectedCard.card,
            difficultyMultiplier = selectedCard.difficultyMultiplier,
            meetChanceMultiplier = selectedCard.meetChanceMultiplier,
            recencyMultiplier = selectedCard.recencyMultiplier,
            selectionProbability = selectionProbability,
        )
    }

    private fun calculateWeightedCard(card: Card): WeightedCard {
        val answers =
            answerRepository
                .findByCardIdOrderByCreatedAtDesc(card.id)

        val answerResults =
            answers.map {
                AnswerResult(
                    score = it.score,
                    createdAt = it.createdAt,
                )
            }

        val meetChanceMultiplier =
            multiplierService.calculateMeetChanceMultiplier(
                card.meetChance.toDouble(),
            )

        val difficultyMultiplier =
            multiplierService.calculateDifficultyMultiplier(
                answerResults,
            )

        val recencyMultiplier =
            multiplierService.calculateRecencyMultiplier(
                answerResults,
            )

        return WeightedCard(
            card = card,
            difficultyMultiplier = difficultyMultiplier,
            meetChanceMultiplier = meetChanceMultiplier,
            recencyMultiplier = recencyMultiplier,
        )
    }

    private fun selectRandomCard(weightedCards: List<WeightedCard>): WeightedCard {
        val totalWeight = weightedCards.sumOf { it.weight }
        val randomValue = Random.nextDouble() * totalWeight

        var accumulatedWeight = 0.0

        for (weightedCard in weightedCards) {
            accumulatedWeight += weightedCard.weight

            if (randomValue < accumulatedWeight) {
                return weightedCard
            }
        }

        // Protect against floating-point rounding.
        return weightedCards.last()
    }

    private fun calculateSelectionProbability(
        selectedCard: WeightedCard,
        totalWeight: Double,
    ): Double {
        if (totalWeight <= 0.0) {
            return 0.0
        }

        return selectedCard.weight / totalWeight * 100.0
    }

    private fun createAnswerProcessing(
        userId: UUID,
        cardId: UUID,
    ) {
        val processing =
            AnswerProcessing(
                answerId = UUID.randomUUID(),
                userId = userId,
                cardId = cardId,
                startAnswerTime = Instant.now(clock),
                status = AnswerProcessingStatus.QUESTION_SENT,
            )

        answerProcessingRepository.save(processing)
    }
}
