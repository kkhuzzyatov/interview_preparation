package com.backend.card.service

import com.backend.answer.entity.Answer
import com.backend.settings.repository.ScoreColorRepository
import org.springframework.stereotype.Service

@Service
class CardLevelEvaluator(
    private val scoreColorRepository: ScoreColorRepository,
) {
    fun evaluate(answers: List<Answer>): CardLevel {
        if (answers.isEmpty()) {
            return CardLevel.BLUE
        }

        val scoreColors = scoreColorRepository.findAll()

        val sortedAnswers =
            answers.sortedByDescending { it.createdAt }

        val averageScore =
            sortedAnswers
                .map { it.score.coerceIn(0, 10) }
                .average()

        val lastScore =
            sortedAnswers
                .first()
                .score
                .coerceIn(0, 10)

        val redThreshold =
            scoreColors
                .filter { it.colorHex.equals("RED", ignoreCase = true) }
                .maxOfOrNull { it.score }

        val greenThreshold =
            scoreColors
                .filter { it.colorHex.equals("GREEN", ignoreCase = true) }
                .minOfOrNull { it.score }

        return when {
            redThreshold != null &&
                (
                    averageScore <= redThreshold ||
                        lastScore <= redThreshold
                ) -> {
                CardLevel.RED
            }

            greenThreshold != null &&
                averageScore >= greenThreshold &&
                lastScore >= greenThreshold -> {
                CardLevel.GREEN
            }

            else -> {
                CardLevel.YELLOW
            }
        }
    }

    enum class CardLevel {
        BLUE,
        RED,
        YELLOW,
        GREEN,
    }
}
