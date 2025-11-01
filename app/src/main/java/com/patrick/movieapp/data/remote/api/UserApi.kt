package com.patrick.movieapp.data.remote.api

import com.patrick.movieapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("api/user/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserResponse>>

    @GET("api/user/stats")
    suspend fun getUserStats(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserStatsResponse>>

    @PUT("api/user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserResponse>>

    @PUT("api/user/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse<Void>>

}
