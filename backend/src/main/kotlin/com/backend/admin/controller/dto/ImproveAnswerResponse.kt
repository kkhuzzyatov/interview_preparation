package com.backend.answer.dto

import java.util.UUID

data class ImproveAnswerResponse(
    val cardId: UUID,
    val message: String,
    val answer: String,
)
