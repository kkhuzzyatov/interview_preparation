package com.interviewpreparation.domain.model

import java.util.UUID

data class ReviewCard(
    val cardId: UUID,
    val deskId: UUID,
    val deskName: String,
    val question: String,
)
