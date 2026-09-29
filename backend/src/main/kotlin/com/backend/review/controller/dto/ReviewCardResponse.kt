package com.backend.review.controller.dto

import java.math.BigDecimal
import java.util.UUID

data class ReviewCardResponse(
    val cardId: UUID,
    val deskId: UUID,
    val deskName: String,
    val question: String,
    val meetChance: BigDecimal,
    val difficultyMultiplier: Double,
    val meetChanceMultiplier: Double,
    val recencyMultiplier: Double,
    val selectionProbability: Double,
)
