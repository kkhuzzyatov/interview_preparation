package com.interviewpreparation.domain.model

import java.util.UUID

data class Card(
    val id: UUID,
    val question: String,
    val answer: String,
    val meetChance: Double,
)
