package com.backend.answer.controller.dto

import java.time.LocalDateTime
import java.util.UUID

data class AnswerHistoryResponse(
    val id: UUID,
    val cardId: UUID,
    val question: String,
    val correctAnswer: String,
    val userAnswer: String,
    val aiFeedback: String,
    val score: Int,
    val startAnswerTime: LocalDateTime,
    val submissionTime: LocalDateTime,
    val aiProcessingDurationMs: Long,
    val createdAt: LocalDateTime,
    val deskId: String,
    val deskName: String,
)
