package com.backend.review.controller.dto

import java.util.UUID

data class ReviewCardResponse(
    val cardId: UUID,
    val deskId: UUID,
    val deskName: String,
    val question: String,
)
