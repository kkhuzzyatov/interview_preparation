package com.backend.leaderboard.repository

import com.backend.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LeaderboardRepository : JpaRepository<User, UUID> {
    @Query(
        """
        SELECT
            u.id AS userId,
            u.username AS username,
            COALESCE(SUM(a.score), 0) AS totalScore,
            COUNT(a.id) AS answerCount
        FROM User u
        LEFT JOIN Answer a
            ON a.user.id = u.id
            AND a.submissionTime >= CURRENT_TIMESTAMP - 1 DAY
        GROUP BY u.id, u.username
        ORDER BY totalScore DESC, answerCount DESC, u.username ASC
        """,
    )
    fun findDay(): List<UserLeaderboardProjection>

    @Query(
        """
        SELECT
            u.id AS userId,
            u.username AS username,
            COALESCE(SUM(a.score), 0) AS totalScore,
            COUNT(a.id) AS answerCount
        FROM User u
        LEFT JOIN Answer a
            ON a.user.id = u.id
            AND a.submissionTime >= CURRENT_TIMESTAMP - 7 DAY
        GROUP BY u.id, u.username
        ORDER BY totalScore DESC, answerCount DESC, u.username ASC
        """,
    )
    fun findWeek(): List<UserLeaderboardProjection>

    @Query(
        """
        SELECT
            u.id AS userId,
            u.username AS username,
            COALESCE(SUM(a.score), 0) AS totalScore,
            COUNT(a.id) AS answerCount
        FROM User u
        LEFT JOIN Answer a
            ON a.user.id = u.id
        GROUP BY u.id, u.username
        ORDER BY totalScore DESC, answerCount DESC, u.username ASC
        """,
    )
    fun findAllTime(): List<UserLeaderboardProjection>
}
