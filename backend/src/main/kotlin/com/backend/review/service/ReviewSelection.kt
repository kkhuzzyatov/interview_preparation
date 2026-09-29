package com.backend.review.service

import com.backend.card.entity.Card

data class ReviewSelection(
    val card: Card,
    val difficultyMultiplier: Double,
    val meetChanceMultiplier: Double,
    val recencyMultiplier: Double,
    val selectionProbability: Double,
)
