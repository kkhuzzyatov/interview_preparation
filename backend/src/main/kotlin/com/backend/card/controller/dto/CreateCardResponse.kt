package com.backend.card.controller.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateCardResponse(
    val id: UUID,
    val question: String,
    val answer: String,
    val deskId: UUID,
    val meetChance: BigDecimal,
)
