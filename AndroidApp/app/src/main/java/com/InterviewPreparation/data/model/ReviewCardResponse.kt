package com.interviewpreparation.data.model

import com.interviewpreparation.domain.model.ReviewCard
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReviewCardResponse(
    val cardId: String,
    val deskId: String,
    val deskName: String,
    val question: String,
) {
    fun toDomain(): ReviewCard =
        ReviewCard(
            cardId = UUID.fromString(cardId),
            deskId = UUID.fromString(deskId),
            deskName = deskName,
            question = question,
        )
}
