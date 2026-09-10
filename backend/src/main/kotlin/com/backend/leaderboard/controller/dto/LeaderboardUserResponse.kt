package com.backend.leaderboard.controller.dto

import java.util.UUID

data class LeaderboardUserResponse(
    val userId: UUID,
    val username: String,
    val totalScore: Long,
    val answerCount: Long,
)
