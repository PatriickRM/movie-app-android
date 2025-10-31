package com.patrick.movieapp.data.repository

import com.google.gson.Gson
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.*

import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UserRepository(private val tokenManager: TokenManager) {

    private val userApi = RetrofitInstance.userApi

    // Obtener usuario actual
    fun getCurrentUser(): Flow<Resource<UserResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = userApi.getCurrentUser(token)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error al obtener usuario"))
                }
            } else {
                emit(Resource.Error("Error al cargar perfil"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener estadísticas
    fun getUserStats(): Flow<Resource<UserStatsResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = userApi.getUserStats(token)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error al obtener estadísticas"))
                }
            } else {
                emit(Resource.Error("Error al cargar estadísticas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Actualizar perfil
    fun updateProfile(request: UpdateProfileRequest): Flow<Resource<UserResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = userApi.updateProfile(token, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error al actualizar perfil"))
                }
            } else {
                emit(Resource.Error("Error al actualizar perfil"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Cambiar contraseña
    fun changePassword(request: ChangePasswordRequest): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = userApi.changePassword(token, request)

            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Error al cambiar contraseña"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }
}