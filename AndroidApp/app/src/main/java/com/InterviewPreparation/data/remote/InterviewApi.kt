package com.interviewpreparation.data.remote

import com.interviewpreparation.data.model.AnswerRequest
import com.interviewpreparation.data.model.AnswerResponse
import com.interviewpreparation.data.model.DeskResponse
import com.interviewpreparation.data.model.DeskStatisticsResponse
import com.interviewpreparation.data.model.DeskWithCardsResponse
import com.interviewpreparation.data.model.ReviewCardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface InterviewApi {
    @GET("api/desks")
    suspend fun getDesks(): List<DeskResponse>

    @GET("api/desks/{deskId}")
    suspend fun getDesk(
        @Path("deskId") deskId: UUID,
    ): DeskWithCardsResponse

    @GET("api/desks/statistics")
    suspend fun getStatistics(): List<DeskStatisticsResponse>

    @GET("api/review/next")
    suspend fun getNextReviewCard(): Response<ReviewCardResponse>

    @POST("api/answer/{cardId}")
    suspend fun answer(
        @Path("cardId") cardId: UUID,
        @Body request: AnswerRequest,
    ): Response<AnswerResponse>
}
