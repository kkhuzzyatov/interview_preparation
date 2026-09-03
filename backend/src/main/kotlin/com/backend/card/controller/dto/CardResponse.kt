package com.backend.card.controller.dto

import java.math.BigDecimal
import java.util.UUID

data class CardResponse(
    val id: UUID,
    val question: String,
    val answer: String,
    val meetChance: BigDecimal,
)
