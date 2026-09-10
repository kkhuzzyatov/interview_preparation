package com.backend.leaderboard.repository

import java.util.UUID

interface UserLeaderboardProjection {
    val userId: UUID
    val username: String
    val totalScore: Long
    val answerCount: Long
}
