package com.patrick.movieapp.data.remote.api

import com.patrick.movieapp.data.remote.dto.ApiResponse
import com.patrick.movieapp.data.remote.dto.AuthResponse
import com.patrick.movieapp.data.remote.dto.LoginRequest
import com.patrick.movieapp.data.remote.dto.RefreshTokenRequest
import com.patrick.movieapp.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<Void>>
}