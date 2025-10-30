package com.patrick.movieapp.data.remote.dto

// Request para agregar rating
data class AddRatingRequest(
    val movieId: Int,
    val rating: Double, // 1.0 a 5.0
    val review: String?
)

// Request para actualizar rating
data class UpdateRatingRequest(
    val rating: Double,
    val review: String?
)

// Response de rating
data class RatingResponse(
    val id: Long,
    val movieId: Int,
    val rating: Double,
    val review: String?,
    val watchedAt: String,
    val updatedAt: String
)