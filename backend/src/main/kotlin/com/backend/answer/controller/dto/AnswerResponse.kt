package com.backend.answer.controller.dto

data class AnswerResponse(
    val score: Int,
    val feedback: String,
    val correctAnswer: String,
)
