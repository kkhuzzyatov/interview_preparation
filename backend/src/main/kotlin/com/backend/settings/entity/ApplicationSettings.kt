package com.backend.settings.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "application_settings")
class ApplicationSettings(
    @Id
    @Column(name = "id")
    var id: Long,
    @Column(name = "answer_evaluation_prompt", nullable = false, columnDefinition = "TEXT")
    var answerEvaluationPrompt: String,
    @Column(name = "evaluation_red_average_score", nullable = false)
    var evaluationRedAverageScore: Double,
    @Column(name = "evaluation_green_min_answers", nullable = false)
    var evaluationGreenMinAnswers: Int,
    @Column(name = "evaluation_green_average_score", nullable = false)
    var evaluationGreenAverageScore: Double,
    @Column(name = "review_multiplier_recency_default_multiplier", nullable = false)
    var reviewMultiplierRecencyDefaultMultiplier: Double,
    @Column(name = "review_multiplier_meet_chance_min", nullable = false)
    var reviewMultiplierMeetChanceMin: Double,
    @Column(name = "review_multiplier_meet_chance_max", nullable = false)
    var reviewMultiplierMeetChanceMax: Double,
    @Column(name = "review_multiplier_min_score", nullable = false)
    var reviewMultiplierMinScore: Int,
    @Column(name = "review_multiplier_max_score", nullable = false)
    var reviewMultiplierMaxScore: Int,
    @Column(name = "review_multiplier_base_difficulty_multiplier", nullable = false)
    var reviewMultiplierBaseDifficultyMultiplier: Double,
    @Column(name = "review_multiplier_default_difficulty_multiplier", nullable = false)
    var reviewMultiplierDefaultDifficultyMultiplier: Double,
)
