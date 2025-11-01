package com.patrick.movieapp.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.tmdb.Cast
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovieDetails
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbVideo
import com.patrick.movieapp.data.repository.MovieDetailsRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MovieDetailsViewModel(
    private val repository: MovieDetailsRepository
) : ViewModel() {

    private val _movieDetails = MutableLiveData<Resource<TMDbMovieDetails>>()
    val movieDetails: LiveData<Resource<TMDbMovieDetails>> = _movieDetails

    private val _movieVideos = MutableLiveData<Resource<List<TMDbVideo>>>()
    val movieVideos: LiveData<Resource<List<TMDbVideo>>> = _movieVideos

    private val _movieCast = MutableLiveData<Resource<List<Cast>>>()
    val movieCast: LiveData<Resource<List<Cast>>> = _movieCast

    private val _similarMovies = MutableLiveData<Resource<List<TMDbMovie>>>()
    val similarMovies: LiveData<Resource<List<TMDbMovie>>> = _similarMovies

    fun loadMovieDetails(movieId: Int) {
        repository.getMovieDetails(movieId)
            .onEach { result ->
                _movieDetails.value = result
            }
            .launchIn(viewModelScope)
    }

    fun loadMovieVideos(movieId: Int) {
        repository.getMovieVideos(movieId)
            .onEach { result ->
                _movieVideos.value = result
            }
            .launchIn(viewModelScope)
    }

    fun loadMovieCast(movieId: Int) {
        repository.getMovieCredits(movieId)
            .onEach { result ->
                _movieCast.value = result
            }
            .launchIn(viewModelScope)
    }

    fun loadSimilarMovies(movieId: Int) {
        repository.getSimilarMovies(movieId)
            .onEach { result ->
                _similarMovies.value = result
            }
            .launchIn(viewModelScope)
    }
}
