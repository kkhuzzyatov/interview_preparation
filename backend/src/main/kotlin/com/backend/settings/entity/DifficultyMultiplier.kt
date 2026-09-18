package com.backend.settings.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "difficulty_multipliers")
class DifficultyMultiplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "difficulty_multiplier_id")
    var difficultyMultiplierId: Long,
    @Column(name = "last_answer_score_border", nullable = false)
    var lastAnswerScoreBorder: Int,
    @Column(name = "multiplier", nullable = false)
    var multiplier: Double,
)
