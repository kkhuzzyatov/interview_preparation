package com.backend.desk.service

import com.backend.answer.repository.AnswerRepository
import com.backend.card.service.CardLevelEvaluator
import com.backend.desk.controller.dto.DeskCardLevelStatisticsResponse
import com.backend.desk.controller.dto.ScoreColorStatistics
import com.backend.desk.repository.DeskRepository
import com.backend.settings.repository.ScoreColorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeskStatisticsService(
    private val deskRepository: DeskRepository,
    private val answerRepository: AnswerRepository,
    private val cardLevelEvaluator: CardLevelEvaluator,
    private val scoreColorRepository: ScoreColorRepository,
) {
    @Transactional(readOnly = true)
    fun getStatistics(): List<DeskCardLevelStatisticsResponse> {
        val scoreColors =
            scoreColorRepository
                .findAll()
                .sortedBy { it.score }

        val result = mutableListOf<DeskCardLevelStatisticsResponse>()

        for (desk in deskRepository.findAll()) {
            val colorCounts = mutableMapOf<String, Int>()

            for (scoreColor in scoreColors) {
                colorCounts[scoreColor.colorHex] = 0
            }

            for (card in desk.cards) {
                val answers =
                    answerRepository
                        .findByCardIdOrderByCreatedAtDesc(card.id)

                if (answers.isEmpty()) {
                    continue
                }

                // Keep CardLevelEvaluator involved in determining the card state.
                cardLevelEvaluator.evaluate(answers)

                val averageScore = calculateAverageScore(answers)
                val color = findColor(averageScore, scoreColors)

                colorCounts[color] = (colorCounts[color] ?: 0) + 1
            }

            val statistics = mutableListOf<ScoreColorStatistics>()

            for (scoreColor in scoreColors) {
                statistics.add(
                    ScoreColorStatistics(
                        colorHex = scoreColor.colorHex,
                        count = colorCounts[scoreColor.colorHex] ?: 0,
                    ),
                )
            }

            statistics.add(
                ScoreColorStatistics(
                    colorHex = "#2563eb",
                    count = desk.cards.size - statistics.sumOf { it.count },
                ),
            )

            result.add(
                DeskCardLevelStatisticsResponse(
                    deskId = desk.id,
                    deskName = desk.name,
                    statistics = statistics,
                ),
            )
        }

        return result
    }

    private fun calculateAverageScore(answers: List<com.backend.answer.entity.Answer>): Double {
        var totalScore = 0.0

        for (answer in answers) {
            totalScore += answer.score.coerceIn(0, 10)
        }

        return totalScore / answers.size
    }

    private fun findColor(
        averageScore: Double,
        scoreColors: List<com.backend.settings.entity.ScoreColor>,
    ): String {
        require(scoreColors.isNotEmpty()) {
            "Score color configuration must not be empty"
        }

        var selectedColor = scoreColors.first().colorHex

        for (scoreColor in scoreColors) {
            if (averageScore >= scoreColor.score) {
                selectedColor = scoreColor.colorHex
            } else {
                break
            }
        }

        return selectedColor
    }
}
