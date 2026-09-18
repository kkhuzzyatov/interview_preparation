package com.backend.settings.controller.dto

data class SettingsRequestDto(
    val answerEvaluationPrompt: String,
    val evaluationRedAverageScore: Double,
    val evaluationGreenMinAnswers: Int,
    val evaluationGreenAverageScore: Double,
    val reviewMultiplierRecencyDefaultMultiplier: Double,
    val reviewMultiplierMeetChanceMin: Double,
    val reviewMultiplierMeetChanceMax: Double,
    val reviewMultiplierMinScore: Int,
    val reviewMultiplierMaxScore: Int,
    val reviewMultiplierBaseDifficultyMultiplier: Double,
    val reviewMultiplierDefaultDifficultyMultiplier: Double,
)
