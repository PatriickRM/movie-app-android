package com.patrick.movieapp.data.repository

import com.google.gson.Gson
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.*
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class RatingRepository(private val tokenManager: TokenManager) {
    private val ratingApi = RetrofitInstance.ratingApi

    // Agregar o actualizar rating
    fun addOrUpdateRating(
        movieId: Int,
        rating: Double,
        review: String?
    ): Flow<Resource<RatingResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val request = AddRatingRequest(movieId, rating, review)

            val response = ratingApi.addOrUpdateRating(token, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
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
                    "Error al guardar calificación"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener ratings del usuario
    fun getUserRatings(page: Int = 0, size: Int = 20): Flow<Resource<PageResponse<RatingResponse>>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = ratingApi.getUserRatings(token, page, size)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar calificaciones"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener rating de una película específica
    fun getRatingByMovie(movieId: Int): Flow<Resource<RatingResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = ratingApi.getRatingByMovie(token, movieId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Calificación no encontrada"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Actualizar rating existente
    fun updateRating(
        movieId: Int,
        rating: Double,
        review: String?
    ): Flow<Resource<RatingResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val request = UpdateRatingRequest(rating, review)

            val response = ratingApi.updateRating(token, movieId, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al actualizar calificación"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Eliminar rating
    fun deleteRating(movieId: Int): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = ratingApi.deleteRating(token, movieId)

            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Error al eliminar calificación"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Verificar si ya calificó
    fun hasRated(movieId: Int): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = ratingApi.hasRated(token, movieId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Success(false))
                }
            } else {
                emit(Resource.Success(false))
            }

        } catch (e: Exception) {
            emit(Resource.Success(false))
        }
    }

    // Obtener IDs de películas calificadas
    fun getRatedMovieIds(): Flow<Resource<List<Int>>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = ratingApi.getRatedMovieIds(token)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar IDs"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }
}