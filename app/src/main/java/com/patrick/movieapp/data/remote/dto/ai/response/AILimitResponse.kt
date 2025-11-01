package com.patrick.movieapp.data.remote.dto.ai.response

import com.google.gson.annotations.SerializedName

data class AILimitResponse(
    @SerializedName("canRequest")
    val canRequest: Boolean,

    @SerializedName("requestsRemainingToday")
    val requestsRemainingToday: Int?,

    @SerializedName("isPremium")
    val isPremium: Boolean,

    @SerializedName("message")
    val message: String?
)
