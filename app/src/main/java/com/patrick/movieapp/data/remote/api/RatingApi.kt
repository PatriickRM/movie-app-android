package com.patrick.movieapp.data.remote.api

import com.patrick.movieapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RatingApi {
    @POST("api/ratings")
    suspend fun addOrUpdateRating(
        @Header("Authorization") token: String,
        @Body request: AddRatingRequest
    ): Response<ApiResponse<RatingResponse>>

    @GET("api/ratings")
    suspend fun getUserRatings(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<RatingResponse>>>

    @GET("api/ratings/movie/{movieId}")
    suspend fun getRatingByMovie(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int
    ): Response<ApiResponse<RatingResponse>>

    @PUT("api/ratings/{movieId}")
    suspend fun updateRating(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int,
        @Body request: UpdateRatingRequest
    ): Response<ApiResponse<RatingResponse>>

    @DELETE("api/ratings/{movieId}")
    suspend fun deleteRating(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int
    ): Response<ApiResponse<Void>>

    @GET("api/ratings/check/{movieId}")
    suspend fun hasRated(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int
    ): Response<ApiResponse<Boolean>>

    @GET("api/ratings/ids")
    suspend fun getRatedMovieIds(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Int>>>
}