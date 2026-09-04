package com.interviewpreparation.data.model

import com.interviewpreparation.domain.model.AnswerResult
import kotlinx.serialization.Serializable

@Serializable
data class AnswerResponse(
    val score: Int,
    val feedback: String,
    val correctAnswer: String,
) {
    fun toDomain(): AnswerResult =
        AnswerResult(
            score = score,
            feedback = feedback,
            correctAnswer = correctAnswer,
        )
}
