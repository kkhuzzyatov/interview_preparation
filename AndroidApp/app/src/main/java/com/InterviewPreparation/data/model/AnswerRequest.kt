package com.interviewpreparation.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnswerRequest(
    val answer: String,
)
