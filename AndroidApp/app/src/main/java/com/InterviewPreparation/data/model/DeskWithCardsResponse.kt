package com.interviewpreparation.data.model

import com.interviewpreparation.domain.model.DeskWithCards
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeskWithCardsResponse(
    val id: String,
    val name: String,
    val cards: List<CardResponse>,
) {
    fun toDomain(): DeskWithCards =
        DeskWithCards(
            id = UUID.fromString(id),
            name = name,
            cards = cards.map { it.toDomain() },
        )
}
