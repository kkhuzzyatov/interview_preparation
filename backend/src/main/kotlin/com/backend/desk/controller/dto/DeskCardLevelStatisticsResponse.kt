package com.backend.desk.controller.dto

import java.util.UUID

data class DeskCardLevelStatisticsResponse(
    val deskId: UUID,
    val deskName: String,
    val blue: Int,
    val red: Int,
    val yellow: Int,
    val green: Int,
)
