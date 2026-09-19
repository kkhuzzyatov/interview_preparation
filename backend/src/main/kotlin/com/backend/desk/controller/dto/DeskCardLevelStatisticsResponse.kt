package com.backend.desk.controller.dto

import java.util.UUID

data class DeskCardLevelStatisticsResponse(
    val deskId: UUID,
    val deskName: String,
    val statistics: List<ScoreColorStatistics>,
)
