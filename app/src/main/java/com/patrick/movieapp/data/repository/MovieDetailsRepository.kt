package com.patrick.movieapp.data.repository

import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.tmdb.Cast
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovieDetails
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbVideo
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieDetailsRepository {

    private val tmdbApi = RetrofitInstance.tmdbApi
    private val apiKey = BuildConfig.TMDB_API_KEY

    fun getMovieDetails(movieId: Int): Flow<Resource<TMDbMovieDetails>> = flow {
        try {
            emit(Resource.Loading())

            // Intentar primero en español
            val responseEs = tmdbApi.getMovieDetails(movieId, apiKey, "es-ES")

            val movieDetails = if (responseEs.isSuccessful && responseEs.body() != null) {
                val details = responseEs.body()!!

                // Si no hay sinopsis en español, probar con inglés
                if (details.overview.isNullOrBlank()) {
                    val responseEn = tmdbApi.getMovieDetails(movieId, apiKey, "en-US")
                    if (responseEn.isSuccessful && responseEn.body() != null) {
                        responseEn.body()!!
                    } else {
                        details // Si también falla en inglés, usamos el original
                    }
                } else {
                    details
                }
            } else {
                throw Exception("Error al obtener detalles en español")
            }

            emit(Resource.Success(movieDetails))

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }


    fun getMovieVideos(movieId: Int): Flow<Resource<List<TMDbVideo>>> = flow {
        try {
            emit(Resource.Loading())

            // 🔹 Primero intenta en español
            val responseEs = tmdbApi.getMovieVideos(movieId, apiKey, "es-ES")

            val videos = if (responseEs.isSuccessful && !responseEs.body()?.results.isNullOrEmpty()) {
                responseEs.body()!!.results
            } else {
                // 🔹 Si no hay en español, intenta en inglés
                val responseEn = tmdbApi.getMovieVideos(movieId, apiKey, "en-US")
                if (responseEn.isSuccessful && responseEn.body() != null) {
                    responseEn.body()!!.results
                } else {
                    emptyList()
                }
            }

            emit(Resource.Success(videos))

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    fun getMovieCredits(movieId: Int): Flow<Resource<List<Cast>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.getMovieCredits(movieId, apiKey)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.cast.take(10))) // Solo 10 actores
            } else {
                emit(Resource.Error("Error al cargar reparto"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    fun getSimilarMovies(movieId: Int): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.getSimilarMovies(movieId, apiKey)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al cargar similares"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }
}