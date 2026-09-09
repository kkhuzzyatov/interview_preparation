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
    fun getNextCard(principal: Principal): Card {
        val userId = UUID.fromString(principal.name)

        val user =
            userRepository
                .findById(userId)
                .orElseThrow {
                    IllegalStateException(
                        "Authenticated user $userId does not exist",
                    )
                }

        /*
         * Do not allow another question while the user already
         * has an answer in progress.
         */
        val existingProcessing =
            answerProcessingRepository.findByUserId(user.id)

        if (existingProcessing != null) {
            return cardRepository
                .findById(existingProcessing.cardId)
                .orElseThrow {
                    IllegalStateException(
                        "Card ${existingProcessing.cardId} for answer " +
                            "${existingProcessing.answerId} does not exist",
                    )
                }
        }

        val cards = cardRepository.findAll()

        if (cards.isEmpty()) {
            throw NoCardsAvailableException()
        }

        val weightedCards =
            cards
                .map { card ->
                    val answers =
                        answerRepository
                            .findByCardIdOrderByCreatedAtDesc(card.id)

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

        val card = selectRandomCard(weightedCards)

        /*
         * Create the temporary answer state as soon as the
         * question is selected.
         *
         * Redis will contain:
         *
         * answer-processing:{answerId}
         * answer-processing:user:{userId}
         *
         * with status = QUESTION_SENT.
         */
        val processing =
            AnswerProcessing(
                answerId = UUID.randomUUID(),
                userId = user.id,
                cardId = card.id,
                startAnswerTime = Instant.now(clock),
                status = AnswerProcessingStatus.QUESTION_SENT,
            )

        answerProcessingRepository.save(processing)

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
}
