package com.backend.card.controller.dto

import java.util.UUID

data class UpdateCardResponse(
    val id: UUID,
    val question: String,
    val answer: String,
    val deskId: UUID,
)
