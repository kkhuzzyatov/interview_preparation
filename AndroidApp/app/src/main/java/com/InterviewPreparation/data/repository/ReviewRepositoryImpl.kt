package com.interviewpreparation.data.repository

import com.interviewpreparation.data.model.AnswerRequest
import com.interviewpreparation.data.remote.InterviewApi
import com.interviewpreparation.domain.model.AnswerResult
import com.interviewpreparation.domain.model.ReviewCard
import com.interviewpreparation.domain.repository.ReviewRepository
import java.util.UUID

class ReviewRepositoryImpl(
    private val api: InterviewApi,
) : ReviewRepository {
    override suspend fun getNextCard(): ReviewCard? {
        val response = api.getNextReviewCard()

        if (response.code() == 404) {
            return null
        }

        if (!response.isSuccessful) {
            throw RuntimeException(
                "Failed to load review card: ${response.code()}",
            )
        }

        return response.body()?.toDomain()
    }

    override suspend fun answer(
        cardId: UUID,
        answer: String,
    ): AnswerResult {
        val response =
            api.answer(
                cardId = cardId,
                request = AnswerRequest(answer),
            )

        if (!response.isSuccessful) {
            throw RuntimeException(
                "Failed to submit answer: ${response.code()}",
            )
        }

        return requireNotNull(response.body()).toDomain()
    }
}
