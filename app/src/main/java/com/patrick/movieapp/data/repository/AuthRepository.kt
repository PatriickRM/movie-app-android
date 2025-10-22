package com.patrick.movieapp.data.repository

import com.google.gson.Gson
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.AuthResponse
import com.patrick.movieapp.data.remote.dto.ErrorResponse
import com.patrick.movieapp.data.remote.dto.LoginRequest
import com.patrick.movieapp.data.remote.dto.RegisterRequest
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val tokenManager: TokenManager) {
    private val authApi = RetrofitInstance.authApi

    // Register
    fun register(email: String, password: String, fullName: String?): Flow<Resource<AuthResponse>> = flow {
        try {
            emit(Resource.Loading())
            val request = RegisterRequest(email, password, fullName)
            val response = authApi.register(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                if (body.success && body.data != null) {
                    // Guardar tokens
                    tokenManager.saveTokens(
                        body.data.accessToken,
                        body.data.refreshToken
                    )

                    // Guardar info de usuario
                    tokenManager.saveUserInfo(
                        body.data.user.id,
                        body.data.user.email
                    )

                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                // Parsear error del backend
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Error de conexión"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Login
    fun login(email: String, password: String): Flow<Resource<AuthResponse>> = flow {
        try {
            emit(Resource.Loading())

            val request = LoginRequest(email, password)
            val response = authApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                if (body.success && body.data != null) {
                    // Guardar tokens
                    tokenManager.saveTokens(
                        body.data.accessToken,
                        body.data.refreshToken
                    )

                    // Guardar info de usuario
                    tokenManager.saveUserInfo(
                        body.data.user.id,
                        body.data.user.email
                    )

                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Credenciales inválidas"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Logout
    suspend fun logout() {
        tokenManager.clearTokens()
    }

    // Verificar si está logueado
    fun isLoggedIn(): Flow<Boolean> = tokenManager.isLoggedIn()
}
