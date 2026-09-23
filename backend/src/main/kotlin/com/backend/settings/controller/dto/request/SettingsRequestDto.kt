package com.backend.settings.controller.dto.request

data class SettingsRequestDto(
    val answerEvaluationPrompt: String,
    val newCardRecencyMultiplier: Double,
    val newCardDifficultyMultiplier: Double,
    val newCardColor: String,
    val difficultyMultipliers: List<DifficultyMultiplierRequestDto>,
    val meetChanceMultipliers: List<MeetChanceMultiplierRequestDto>,
    val recencyMultipliers: List<RecencyMultiplierRequestDto>,
    val scoreColor: List<ScoreColorRequestDto>,
)
