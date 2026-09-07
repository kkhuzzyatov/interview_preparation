package com.backend.answer.controller.dto

import java.time.LocalDateTime
import java.util.UUID

data class AnswerHistoryResponse(
    val id: UUID,
    val cardId: UUID,
    val question: String,
    val correctAnswer: String,
    val score: Int,
    val createdAt: LocalDateTime,
)
