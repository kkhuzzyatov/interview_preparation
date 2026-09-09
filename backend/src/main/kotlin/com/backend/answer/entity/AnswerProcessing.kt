package com.backend.answer.entity

import java.time.Instant
import java.util.UUID

data class AnswerProcessing(
    val answerId: UUID,
    val userId: UUID,
    val cardId: UUID,
    val startAnswerTime: Instant,
    val userAnswer: String? = null,
    val submissionTime: Instant? = null,
    val aiProcessingStartTime: Instant? = null,
    val status: AnswerProcessingStatus,
)
