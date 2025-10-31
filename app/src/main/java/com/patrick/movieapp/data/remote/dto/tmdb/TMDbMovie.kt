package com.patrick.movieapp.data.remote.dto.tmdb

import com.google.gson.annotations.SerializedName

data class TMDbMovie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int
)

data class TMDbMovieListResponse(
    val page: Int,
    val results: List<TMDbMovie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TMDbMovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("runtime")val runtime: Int?,
    @SerializedName("genres")val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)
data class TMDbVideoResponse(
    val id: Int,
    val results: List<TMDbVideo>
)
//Trailer
data class TMDbVideo(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    @SerializedName("published_at") val publishedAt: String
)

// Credits (Cast)
data class TMDbCreditsResponse(
    val id: Int,
    val cast: List<Cast>
)

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class TMDbGenreListResponse(
    val genres: List<Genre>
)
