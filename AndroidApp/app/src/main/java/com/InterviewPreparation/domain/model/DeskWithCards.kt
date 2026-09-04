package com.interviewpreparation.domain.model

import java.util.UUID

data class DeskWithCards(
    val id: UUID,
    val name: String,
    val cards: List<Card>,
)
