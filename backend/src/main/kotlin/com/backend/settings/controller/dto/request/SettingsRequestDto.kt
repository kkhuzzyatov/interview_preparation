package com.backend.settings.controller.dto.request

data class SettingsRequestDto(
    val answerEvaluationPrompt: String,
    val difficultyMultipliers: List<DifficultyMultiplierRequestDto>,
    val meetChanceMultipliers: List<MeetChanceMultiplierRequestDto>,
    val recencyMultipliers: List<RecencyMultiplierRequestDto>,
    val scoreColor: List<ScoreColorRequestDto>,
)
