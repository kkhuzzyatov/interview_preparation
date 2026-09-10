package com.backend.leaderboard.service

import com.backend.leaderboard.controller.dto.LeaderboardUserResponse
import com.backend.leaderboard.repository.LeaderboardRepository
import com.backend.leaderboard.repository.UserLeaderboardProjection
import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
) {
    fun getDay(): List<LeaderboardUserResponse> =
        leaderboardRepository
            .findDay()
            .map { it.toResponse() }

    fun getWeek(): List<LeaderboardUserResponse> =
        leaderboardRepository
            .findWeek()
            .map { it.toResponse() }

    fun getAllTime(): List<LeaderboardUserResponse> =
        leaderboardRepository
            .findAllTime()
            .map { it.toResponse() }

    private fun UserLeaderboardProjection.toResponse(): LeaderboardUserResponse =
        LeaderboardUserResponse(
            userId = userId,
            username = username,
            totalScore = totalScore,
            answerCount = answerCount,
        )
}
