package com.backend.settings.controller.dto.request

data class DifficultyMultiplierRequestDto(
    val lastAnswerScoreBorder: Int,
    val multiplier: Double,
)
