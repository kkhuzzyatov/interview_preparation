package com.interviewpreparation

import retrofit2.http.GET

interface DeskApi {

    @GET("api/desks")
    suspend fun getDesks(): DeskResponse
}