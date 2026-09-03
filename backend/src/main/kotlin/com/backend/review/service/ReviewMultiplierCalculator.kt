package com.backend.review.service

import com.backend.answer.dto.AnswerResult
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

@Service
class ReviewMultiplierCalculator(
    private val clock: Clock,
) {
    fun calculateRecencyMultiplier(answeredAt: LocalDateTime): Double {
        val now = LocalDateTime.now(clock)
        val ageSeconds =
            Duration
                .between(answeredAt, now)
                .seconds
                .coerceAtLeast(0)

        return when {
            ageSeconds <= 10 -> {
                0.0
            }

            ageSeconds <= 150 -> {
                interpolate(
                    x = ageSeconds.toDouble(),
                    x1 = 10.0,
                    y1 = 0.0,
                    x2 = 150.0,
                    y2 = 0.25,
                )
            }

            ageSeconds <= 300 -> {
                interpolate(
                    x = ageSeconds.toDouble(),
                    x1 = 150.0,
                    y1 = 0.25,
                    x2 = 300.0,
                    y2 = 0.5,
                )
            }

            ageSeconds <= 3_600 -> {
                interpolate(
                    x = ageSeconds.toDouble(),
                    x1 = 300.0,
                    y1 = 0.5,
                    x2 = 3_600.0,
                    y2 = 1.0,
                )
            }

            ageSeconds <= 86_400 -> {
                interpolate(
                    x = ageSeconds.toDouble(),
                    x1 = 3_600.0,
                    y1 = 1.0,
                    x2 = 86_400.0,
                    y2 = 1.25,
                )
            }

            else -> {
                1.5
            }
        }
    }

    fun calculateMeetChanceMultiplier(meetChance: Double): Double {
        require(meetChance in 0.0..100.0) {
            "meetChance must be between 0 and 100"
        }

        val points =
            listOf(
                0.32 to 0.0,
                0.96 to 0.5,
                2.56 to 1.0,
                5.45 to 1.5,
                10.80 to 2.0,
                14.05 to 2.5,
                25.96 to 3.0,
            )

        if (meetChance <= points.first().first) {
            return 0.0
        }

        if (meetChance >= points.last().first) {
            return 3.0
        }

        val (lower, upper) =
            points
                .zipWithNext()
                .first { (lower, upper) ->
                    meetChance >= lower.first && meetChance <= upper.first
                }

        return interpolate(
            x = meetChance,
            x1 = lower.first,
            y1 = lower.second,
            x2 = upper.first,
            y2 = upper.second,
        )
    }

    fun calculateDifficultyMultiplier(answers: List<AnswerResult>): Double {
        if (answers.isEmpty()) {
            return 1.5
        }

        val averageScore =
            answers
                .sortedByDescending { it.createdAt }
                .take(MAX_RECENT_ANSWERS)
                .map { it.score.coerceIn(0, 10) }
                .average()

        val difficulty = 1.0 - averageScore / 10.0

        return 1.0 + difficulty
    }

    private fun interpolate(
        x: Double,
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double,
    ): Double = y1 + (x - x1) / (x2 - x1) * (y2 - y1)

    companion object {
        private const val MAX_RECENT_ANSWERS = 5
    }
}
