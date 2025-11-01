package com.patrick.movieapp.data.repository

import com.google.gson.Gson
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.*
import com.patrick.movieapp.data.remote.dto.ai.request.AIRecommendationRequest;
import com.patrick.movieapp.data.remote.dto.ai.response.AILimitResponse
import com.patrick.movieapp.data.remote.dto.ai.response.AIRecommendationResponse;
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class AIRecommendationRepository(private val tokenManager: TokenManager) {

    private val aiApi = RetrofitInstance.aiApi

    // Obtener recomendaciones de IA
    fun getRecommendations(request:AIRecommendationRequest): Flow<Resource<AIRecommendationResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = aiApi.getRecommendation(token, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error al obtener recomendaciones"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Error al obtener recomendaciones de IA"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Verificar límite de IA
    fun checkAILimit(): Flow<Resource<AILimitResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = aiApi.canRequestAI(token)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error al verificar límite"))
                }
            } else {
                emit(Resource.Error("Error al verificar límite de IA"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }
}