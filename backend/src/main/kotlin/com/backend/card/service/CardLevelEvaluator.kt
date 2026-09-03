package com.backend.card.service

import com.backend.answer.entity.Answer
import org.springframework.stereotype.Service

@Service
class CardLevelEvaluator {
    fun evaluate(answers: List<Answer>): CardLevel {
        if (answers.isEmpty()) {
            return CardLevel.BLUE
        }

        val sortedAnswers =
            answers
                .sortedByDescending { it.createdAt }

        val averageScore =
            sortedAnswers
                .map { it.score.coerceIn(0, 10) }
                .average()

        val recentAverage =
            sortedAnswers
                .take(EvaluationSettings.RECENT_ANSWERS_COUNT)
                .map { it.score.coerceIn(0, 10) }
                .average()

        return when {
            averageScore < EvaluationSettings.RED_AVERAGE_SCORE ||
                recentAverage < EvaluationSettings.RED_RECENT_AVERAGE_SCORE -> {
                CardLevel.RED
            }

            sortedAnswers.size >= EvaluationSettings.GREEN_MIN_ANSWERS &&
                averageScore >= EvaluationSettings.GREEN_AVERAGE_SCORE &&
                recentAverage >= EvaluationSettings.GREEN_RECENT_AVERAGE_SCORE -> {
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
