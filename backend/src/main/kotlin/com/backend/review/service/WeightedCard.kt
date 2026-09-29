package com.backend.review.service

import com.backend.card.entity.Card

data class WeightedCard(
    val card: Card,
    val difficultyMultiplier: Double,
    val meetChanceMultiplier: Double,
    val recencyMultiplier: Double,
) {
    val weight: Double
        get() =
            difficultyMultiplier *
                meetChanceMultiplier *
                recencyMultiplier
}
