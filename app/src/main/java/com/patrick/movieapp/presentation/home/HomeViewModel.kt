package com.patrick.movieapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.data.repository.MovieRepository
import com.patrick.movieapp.utils.Resource

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _trendingMovies = MutableLiveData<Resource<List<TMDbMovie>>>()
    val trendingMovies: LiveData<Resource<List<TMDbMovie>>> = _trendingMovies

    private val _popularMovies = MutableLiveData<Resource<List<TMDbMovie>>>()
    val popularMovies: LiveData<Resource<List<TMDbMovie>>> = _popularMovies

    private val _upcomingMovies = MutableLiveData<Resource<List<TMDbMovie>>>()
    val upcomingMovies: LiveData<Resource<List<TMDbMovie>>> = _upcomingMovies

    private val _topRatedMovies = MutableLiveData<Resource<List<TMDbMovie>>>()
    val topRatedMovies: LiveData<Resource<List<TMDbMovie>>> = _topRatedMovies

    init {
        loadMovies()
    }

    fun loadMovies() {
        loadTrending()
        loadPopular()
        loadUpcoming()
        loadTopRated()
    }

    private fun loadTrending() {
        movieRepository.getTrendingMovies()
            .onEach { result ->
                _trendingMovies.value = result
            }
            .launchIn(viewModelScope)
    }

    private fun loadPopular() {
        movieRepository.getPopularMovies()
            .onEach { result ->
                _popularMovies.value = result
            }
            .launchIn(viewModelScope)
    }

    private fun loadUpcoming() {
        movieRepository.getUpcomingMovies()
            .onEach { result ->
                _upcomingMovies.value = result
            }
            .launchIn(viewModelScope)
    }

    private fun loadTopRated() {
        movieRepository.getTopRatedMovies()
            .onEach { result ->
                _topRatedMovies.value = result
            }
            .launchIn(viewModelScope)
    }
}
