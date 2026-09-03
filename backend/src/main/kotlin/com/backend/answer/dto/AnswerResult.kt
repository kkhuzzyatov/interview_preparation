package com.backend.answer.dto

import java.time.LocalDateTime

data class AnswerResult(
    val score: Int,
    val createdAt: LocalDateTime,
)
