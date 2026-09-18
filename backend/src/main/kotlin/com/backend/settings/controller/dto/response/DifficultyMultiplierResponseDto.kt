package com.backend.settings.controller.dto.response

data class DifficultyMultiplierResponseDto(
    val difficultyMultiplierId: Long,
    val lastAnswerScoreBorder: Int,
    val multiplier: Double,
)
