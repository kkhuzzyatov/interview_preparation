package com.interviewpreparation.data.model

import com.interviewpreparation.domain.model.Card
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CardResponse(
    val id: String,
    val question: String,
    val answer: String,
    val meetChance: Double,
) {
    fun toDomain(): Card =
        Card(
            id = UUID.fromString(id),
            question = question,
            answer = answer,
            meetChance = meetChance,
        )
}
