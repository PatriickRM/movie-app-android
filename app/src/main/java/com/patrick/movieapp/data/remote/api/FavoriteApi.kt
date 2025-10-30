package com.patrick.movieapp.data.remote.api

import com.patrick.movieapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface FavoriteApi {
    @POST("api/favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: AddFavoriteRequest
    ): Response<ApiResponse<FavoriteResponse>>

    @GET("api/favorites")
    suspend fun getUserFavorites(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<FavoriteResponse>>>

    @DELETE("api/favorites/{movieId}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int
    ): Response<ApiResponse<Void>>

    @GET("api/favorites/check/{movieId}")
    suspend fun isFavorite(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: Int
    ): Response<ApiResponse<Boolean>>

    @GET("api/favorites/ids")
    suspend fun getFavoriteIds(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Int>>>

    @GET("api/favorites/stats")
    suspend fun getFavoritesStats(
        @Header("Authorization") token: String
    ): Response<ApiResponse<FavoritesStatsResponse>>

}