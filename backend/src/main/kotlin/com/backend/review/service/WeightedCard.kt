package com.backend.review.service

import com.backend.card.entity.Card

data class WeightedCard(
    val card: Card,
    val weight: Double,
)
