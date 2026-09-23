package com.backend.settings.controller.dto.response

data class SettingsResponseDto(
    val answerEvaluationPrompt: String,
    val newCardRecencyMultiplier: Double,
    val newCardDifficultyMultiplier: Double,
    val newCardColor: String,
    val difficultyMultipliers: List<DifficultyMultiplierResponseDto>,
    val meetChanceMultipliers: List<MeetChanceMultiplierResponseDto>,
    val recencyMultipliers: List<RecencyMultiplierResponseDto>,
    val scoreColor: List<ScoreColorResponseDto>,
)
