package com.patrick.movieapp.data.remote.dto.ai.request

import com.google.gson.annotations.SerializedName

data class AIRecommendationRequest(
    @SerializedName("prompt")
    val prompt: String,

    @SerializedName("includeUserHistory")
    val includeUserHistory: Boolean = true,

    @SerializedName("maxRecommendations")
    val maxRecommendations: Int = 5
)