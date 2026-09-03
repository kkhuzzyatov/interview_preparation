package com.backend.card.service

import com.backend.answer.repository.AnswerRepository
import com.backend.card.repository.CardRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CardLevelStatisticsService(
    private val cardRepository: CardRepository,
    private val answerRepository: AnswerRepository,
    private val cardLevelEvaluator: CardLevelEvaluator,
) {
    fun getStatistics(deskId: UUID): CardLevelStatistics {
        val cards = cardRepository.findByDeskId(deskId)

        val counts =
            cards
                .groupingBy { card ->
                    val answers =
                        answerRepository
                            .findByCardIdOrderByCreatedAtDesc(card.id)

                    cardLevelEvaluator.evaluate(answers)
                }.eachCount()

        return CardLevelStatistics(
            blue = counts[CardLevelEvaluator.CardLevel.BLUE] ?: 0,
            red = counts[CardLevelEvaluator.CardLevel.RED] ?: 0,
            yellow = counts[CardLevelEvaluator.CardLevel.YELLOW] ?: 0,
            green = counts[CardLevelEvaluator.CardLevel.GREEN] ?: 0,
        )
    }
}
