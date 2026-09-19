package com.backend.review.service

import com.backend.answer.dto.AnswerResult
import com.backend.settings.repository.DifficultyMultiplierRepository
import com.backend.settings.repository.MeetChanceMultiplierRepository
import com.backend.settings.repository.RecencyMultiplierRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

@Service
class ReviewMultiplierCalculator(
    private val clock: Clock,
    private val recencyMultiplierRepository: RecencyMultiplierRepository,
    private val meetChanceMultiplierRepository: MeetChanceMultiplierRepository,
    private val difficultyMultiplierRepository: DifficultyMultiplierRepository,
) {
    fun calculateRecencyMultiplier(answeredAt: LocalDateTime): Double {
        val now = LocalDateTime.now(clock)
        val ageSeconds =
            Duration
                .between(answeredAt, now)
                .seconds
                .coerceAtLeast(0)

        val multipliers =
            recencyMultiplierRepository
                .findAll()
                .sortedBy { it.secondsBorder }

        return calculateMultiplier(
            value = ageSeconds.toDouble(),
            points =
                multipliers.map {
                    it.secondsBorder.toDouble() to it.multiplier
                },
        )
    }

    fun calculateMeetChanceMultiplier(meetChance: Double): Double {
        require(meetChance in 0.0..100.0) {
            "meetChance must be between 0 and 100"
        }

        val multipliers =
            meetChanceMultiplierRepository
                .findAll()
                .sortedBy { it.meetChanceBorder }

        return calculateMultiplier(
            value = meetChance,
            points =
                multipliers.map {
                    it.meetChanceBorder to it.multiplier
                },
        )
    }

    fun calculateDifficultyMultiplier(answers: List<AnswerResult>): Double {
        if (answers.isEmpty()) {
            return 1.0
        }

        val recentAnswers =
            answers
                .sortedByDescending { it.createdAt }
                .take(MAX_RECENT_ANSWERS)

        var totalScore = 0.0

        for (answer in recentAnswers) {
            totalScore += answer.score.coerceIn(0, 10)
        }

        val averageScore = totalScore / recentAnswers.size

        val multipliers =
            difficultyMultiplierRepository
                .findAll()
                .sortedBy { it.lastAnswerScoreBorder }

        return calculateMultiplier(
            value = averageScore,
            points =
                multipliers.map {
                    it.lastAnswerScoreBorder.toDouble() to it.multiplier
                },
        )
    }

    private fun calculateMultiplier(
        value: Double,
        points: List<Pair<Double, Double>>,
    ): Double {
        require(points.isNotEmpty()) {
            "Multiplier configuration must not be empty"
        }

        if (value <= points.first().first) {
            return points.first().second
        }

        if (value >= points.last().first) {
            return points.last().second
        }

        for (i in 0 until points.size - 1) {
            val lower = points[i]
            val upper = points[i + 1]

            if (value >= lower.first && value <= upper.first) {
                return interpolate(
                    x = value,
                    x1 = lower.first,
                    y1 = lower.second,
                    x2 = upper.first,
                    y2 = upper.second,
                )
            }
        }

        error("Could not find multiplier range for value: $value")
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
