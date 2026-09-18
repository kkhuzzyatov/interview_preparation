package com.backend.settings.controller.dto.response

data class ScoreColorResponseDto(
    val scoreColorsId: Long,
    val score: Int,
    val colorHex: String,
)
