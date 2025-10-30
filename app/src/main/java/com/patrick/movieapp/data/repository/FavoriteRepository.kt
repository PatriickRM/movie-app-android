package com.patrick.movieapp.data.repository

import com.google.gson.Gson
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.*
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FavoriteRepository(private val tokenManager: TokenManager) {
    private val favoriteApi = RetrofitInstance.favoriteApi

    // Agregar favorito
    fun addFavorite(
        movieId: Int,
        movieTitle: String?,
        moviePoster: String?,
        movieOverview: String?,
        releaseDate: String?,
        voteAverage: Double?
    ): Flow<Resource<FavoriteResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val request = AddFavoriteRequest(
                movieId = movieId,
                movieTitle = movieTitle,
                moviePoster = moviePoster,
                movieOverview = movieOverview,
                releaseDate = releaseDate,
                voteAverage = voteAverage
            )

            val response = favoriteApi.addFavorite(token, request)

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
                    "Error al agregar favorito"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener favoritos con paginación
    fun getUserFavorites(page: Int = 0, size: Int = 20): Flow<Resource<PageResponse<FavoriteResponse>>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = favoriteApi.getUserFavorites(token, page, size)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar favoritos"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Eliminar favorito
    fun removeFavorite(movieId: Int): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = favoriteApi.removeFavorite(token, movieId)

            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Error al eliminar favorito"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Verificar si es favorito
    fun isFavorite(movieId: Int): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = favoriteApi.isFavorite(token, movieId)

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

    // Obtener stats de favoritos
    fun getFavoritesStats(): Flow<Resource<FavoritesStatsResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = favoriteApi.getFavoritesStats(token)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar estadísticas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }
}