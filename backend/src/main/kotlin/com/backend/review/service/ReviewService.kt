package com.backend.review.service

import com.backend.answer.dto.AnswerResult
import com.backend.answer.entity.Answer
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
    ): Card {
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
                    WeightedCard(
                        card = card,
                        weight =
                            calculateWeight(
                                card = card,
                                answers =
                                    answerRepository
                                        .findByCardIdOrderByCreatedAtDesc(card.id),
                            ),
                    )
                }.filter { it.weight > 0.0 }

        if (weightedCards.isEmpty()) {
            throw NoCardsAvailableException()
        }

        val card = selectRandomCard(weightedCards)

        createAnswerProcessing(
            userId = user.id,
            cardId = card.id,
        )

        return card
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
                    multiplierService.calculateRecencyMultiplier(
                        it.createdAt,
                    )
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
