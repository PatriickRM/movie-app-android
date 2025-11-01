package com.patrick.movieapp.data.remote.dto

// Request para crear lista
data class CreateListRequest(
    val name: String,
    val description: String?,
    val isPublic: Boolean = false
)

// Request para actualizar lista
data class UpdateListRequest(
    val name: String?,
    val description: String?,
    val isPublic: Boolean?
)

// Request para agregar película a lista
data class AddMovieToListRequest(
    val movieId: Int,
    val movieTitle: String?,
    val moviePoster: String?
)

// Response de lista
data class CustomListResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val movieCount: Int,
    val createdAt: String,
    val updatedAt: String
)

// Response de lista con películas
data class CustomListDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val movies: List<ListMovieResponse>,
    val createdAt: String,
    val updatedAt: String
)

// Película en lista
data class ListMovieResponse(
    val movieId: Int,
    val movieTitle: String?,
    val moviePoster: String?,
    val addedAt: String
)