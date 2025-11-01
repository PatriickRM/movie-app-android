package com.patrick.movieapp.data.remote.api

import com.patrick.movieapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CustomListApi {
    @POST("api/lists")
    suspend fun createList(
        @Header("Authorization") token: String,
        @Body request: CreateListRequest
    ): Response<ApiResponse<CustomListResponse>>

    @GET("api/lists")
    suspend fun getUserLists(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<CustomListResponse>>>

    @GET("api/lists/{listId}")
    suspend fun getListDetails(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long
    ): Response<ApiResponse<CustomListDetailResponse>>

    @PUT("api/lists/{listId}")
    suspend fun updateList(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long,
        @Body request: UpdateListRequest
    ): Response<ApiResponse<CustomListResponse>>

    @DELETE("api/lists/{listId}")
    suspend fun deleteList(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long
    ): Response<ApiResponse<Void>>

    @POST("api/lists/{listId}/movies")
    suspend fun addMovieToList(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long,
        @Body request: AddMovieToListRequest
    ): Response<ApiResponse<Void>>

    @DELETE("api/lists/{listId}/movies/{movieId}")
    suspend fun removeMovieFromList(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long,
        @Path("movieId") movieId: Int
    ): Response<ApiResponse<Void>>

    @GET("api/lists/public")
    suspend fun getPublicLists(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<CustomListResponse>>>
}
