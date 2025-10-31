package com.patrick.movieapp.data.repository

import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.tmdb.Genre
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepository {
    private val tmdbApi = RetrofitInstance.tmdbApi
    private val apiKey = BuildConfig.TMDB_API_KEY

    // Buscar películas por query
    fun searchMovies(query: String): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.searchMovies(apiKey, query, "es-ES")

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al buscar películas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    // Descubrir películas con filtros
    fun discoverMovies(
        genreId: Int? = null,
        year: Int? = null,
        minRating: Double? = null
    ): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.discoverMovies(
                apiKey = apiKey,
                language = "es-ES",
                genreId = genreId,
                year = year,
                minRating = minRating
            )

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al filtrar películas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    // Obtener lista de géneros
    fun getGenres(): Flow<Resource<List<Genre>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.getGenres(apiKey, "es-ES")

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.genres))
            } else {
                emit(Resource.Error("Error al cargar géneros"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }
}