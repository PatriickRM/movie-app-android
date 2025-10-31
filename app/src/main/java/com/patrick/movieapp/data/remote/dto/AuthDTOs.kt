package com.patrick.movieapp.data.remote.dto

//REQUESTS
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

//RESPONSES

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val timestamp: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: UserResponse
)


data class UserResponse(
    val id: Long,
    val email: String,
    val fullName: String?,
    val plan: String,
    val aiRequestsToday: Int,
    val maxFavorites: Int,
    val premiumUntil: String?,
    val emailVerified: Boolean,
    val createdAt: String
)

//ERROR

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: String
)
data class UserStatsResponse(
    val totalFavorites: Int,
    val totalRatings: Int,
    val totalLists: Int,
    val averageRating: Double,
    val totalAIRequests: Int
)
