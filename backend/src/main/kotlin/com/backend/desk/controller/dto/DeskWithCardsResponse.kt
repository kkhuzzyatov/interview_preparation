package com.backend.desk.controller.dto

import com.backend.card.controller.dto.CardResponse
import java.util.UUID

data class DeskWithCardsResponse(
    val id: UUID,
    val name: String,
    val cards: List<CardResponse>,
)
