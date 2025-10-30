package com.patrick.movieapp.data.remote.dto

data class AddFavoriteRequest(
    val movieId: Int,
    val movieTitle: String?,
    val moviePoster: String?,
    val movieOverview: String?,
    val releaseDate: String?,
    val voteAverage: Double?
)

data class FavoriteResponse(
    val id: Long,
    val movieId: Int,
    val movieTitle: String?,
    val moviePoster: String?,
    val movieOverview: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val addedAt: String
)

data class FavoritesStatsResponse(
    val totalFavorites: Int,
    val maxFavorites: Int,
    val canAddMore: Boolean,
    val isPremium: Boolean
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)
