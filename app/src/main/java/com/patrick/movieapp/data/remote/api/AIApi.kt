package com.patrick.movieapp.data.remote.api

import com.patrick.movieapp.data.remote.dto.*
import com.patrick.movieapp.data.remote.dto.ai.request.AIRecommendationRequest
import com.patrick.movieapp.data.remote.dto.ai.response.AILimitResponse
import com.patrick.movieapp.data.remote.dto.ai.response.AIRecommendationResponse
import retrofit2.Response
import retrofit2.http.*

interface AIApi {

    @POST("api/ai/recommend")
    suspend fun getRecommendation(
        @Header("Authorization") token: String,
        @Body request: AIRecommendationRequest
    ): Response<ApiResponse<AIRecommendationResponse>>

    @GET("api/ai/can-request")
    suspend fun canRequestAI(
        @Header("Authorization") token: String
    ): Response<ApiResponse<AILimitResponse>>
}