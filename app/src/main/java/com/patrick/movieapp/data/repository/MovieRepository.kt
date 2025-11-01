package com.patrick.movieapp.data.repository

import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository {
    private val tmdbApi = RetrofitInstance.tmdbApi
    private val apiKey = BuildConfig.TMDB_API_KEY

    fun getTrendingMovies(): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.getTrendingMovies(apiKey)
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al cargar películas"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    fun getPopularMovies(): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.getPopularMovies(apiKey)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al cargar películas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    fun getUpcomingMovies(): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())
            val response = tmdbApi.getUpcomingMovies(apiKey)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al cargar películas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }

    fun getTopRatedMovies(): Flow<Resource<List<TMDbMovie>>> = flow {
        try {
            emit(Resource.Loading())

            val response = tmdbApi.getTopRatedMovies(apiKey)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!.results))
            } else {
                emit(Resource.Error("Error al cargar películas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error de conexión"))
        }
    }
}
