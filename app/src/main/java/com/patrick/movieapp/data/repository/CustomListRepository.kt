package com.patrick.movieapp.data.repository

import com.google.gson.Gson
import com.patrick.movieapp.data.local.TokenManager
import com.patrick.movieapp.data.remote.RetrofitInstance
import com.patrick.movieapp.data.remote.dto.*
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CustomListRepository(private val tokenManager: TokenManager) {
    private val listApi = RetrofitInstance.customListApi

    // Crear lista
    fun createList(
        name: String,
        description: String?,
        isPublic: Boolean
    ): Flow<Resource<CustomListResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val request = CreateListRequest(name, description, isPublic)

            val response = listApi.createList(token, request)

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
                    "Error al crear lista"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener listas del usuario
    fun getUserLists(page: Int = 0, size: Int = 20): Flow<Resource<PageResponse<CustomListResponse>>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = listApi.getUserLists(token, page, size)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar listas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener detalles de una lista
    fun getListDetails(listId: Long): Flow<Resource<CustomListDetailResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = listApi.getListDetails(token, listId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar detalles"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Actualizar lista
    fun updateList(
        listId: Long,
        name: String?,
        description: String?,
        isPublic: Boolean?
    ): Flow<Resource<CustomListResponse>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val request = UpdateListRequest(name, description, isPublic)

            val response = listApi.updateList(token, listId, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al actualizar lista"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Eliminar lista
    fun deleteList(listId: Long): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = listApi.deleteList(token, listId)

            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Error al eliminar lista"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Agregar película a lista
    fun addMovieToList(
        listId: Long,
        movieId: Int,
        movieTitle: String?,
        moviePoster: String?
    ): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val request = AddMovieToListRequest(movieId, movieTitle, moviePoster)

            val response = listApi.addMovieToList(token, listId, request)

            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message
                } catch (e: Exception) {
                    "Error al agregar película"
                }
                emit(Resource.Error(errorMessage))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Eliminar película de lista
    fun removeMovieFromList(listId: Long, movieId: Int): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())

            val token = "Bearer ${tokenManager.getAccessToken().first()}"
            val response = listApi.removeMovieFromList(token, listId, movieId)

            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Error al eliminar película"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }

    // Obtener listas públicas
    fun getPublicLists(page: Int = 0, size: Int = 20): Flow<Resource<PageResponse<CustomListResponse>>> = flow {
        try {
            emit(Resource.Loading())

            val response = listApi.getPublicLists(page, size)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.data != null) {
                    emit(Resource.Success(body.data))
                } else {
                    emit(Resource.Error(body.message ?: "Error desconocido"))
                }
            } else {
                emit(Resource.Error("Error al cargar listas públicas"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error inesperado"))
        }
    }
}