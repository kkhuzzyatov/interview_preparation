package com.interviewpreparation.domain.repository

import com.interviewpreparation.domain.model.AnswerResult
import com.interviewpreparation.domain.model.ReviewCard
import java.util.UUID

interface ReviewRepository {
    suspend fun getNextCard(): ReviewCard?

    suspend fun answer(
        cardId: UUID,
        answer: String,
    ): AnswerResult
}
