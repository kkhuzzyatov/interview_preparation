package com.backend.desk.service

import com.backend.answer.repository.AnswerRepository
import com.backend.card.service.CardLevelEvaluator
import com.backend.desk.controller.dto.DeskCardLevelStatisticsResponse
import com.backend.desk.repository.DeskRepository
import org.springframework.stereotype.Service

@Service
class DeskStatisticsService(
    private val deskRepository: DeskRepository,
    private val answerRepository: AnswerRepository,
    private val cardLevelEvaluator: CardLevelEvaluator,
) {
    fun getStatistics(): List<DeskCardLevelStatisticsResponse> =
        deskRepository.findAll().map { desk ->
            val cards = desk.cards

            val counts =
                cards
                    .groupingBy { card ->
                        val answers =
                            answerRepository
                                .findByCardIdOrderByCreatedAtDesc(card.id)

                        cardLevelEvaluator.evaluate(answers)
                    }.eachCount()

            DeskCardLevelStatisticsResponse(
                deskId = desk.id,
                deskName = desk.name,
                blue = counts[CardLevelEvaluator.CardLevel.BLUE] ?: 0,
                red = counts[CardLevelEvaluator.CardLevel.RED] ?: 0,
                yellow = counts[CardLevelEvaluator.CardLevel.YELLOW] ?: 0,
                green = counts[CardLevelEvaluator.CardLevel.GREEN] ?: 0,
            )
        }
}
