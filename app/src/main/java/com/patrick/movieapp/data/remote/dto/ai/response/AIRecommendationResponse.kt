package com.patrick.movieapp.data.remote.dto.ai.response

import com.google.gson.annotations.SerializedName
import com.patrick.movieapp.data.remote.dto.tmdb.MovieRecommendation

data class AIRecommendationResponse(
    @SerializedName("recommendations")
    val recommendations: List<MovieRecommendation>,

    @SerializedName("explanation")
    val explanation: String,

    @SerializedName("requestsRemainingToday")
    val requestsRemainingToday: Int?
)