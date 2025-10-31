package com.patrick.movieapp.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.tmdb.Genre
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.data.repository.SearchRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchViewModel(
    private val searchRepository: SearchRepository) : ViewModel() {
    private val _searchResults = MutableLiveData<Resource<List<TMDbMovie>>>()
    val searchResults: LiveData<Resource<List<TMDbMovie>>> = _searchResults

    private val _genres = MutableLiveData<Resource<List<Genre>>>()
    val genres: LiveData<Resource<List<Genre>>> = _genres

    init {
        loadGenres()
    }

    // Buscar películas por query
    fun searchMovies(query: String) {
        searchRepository.searchMovies(query)
            .onEach { result ->
                _searchResults.value = result
            }
            .launchIn(viewModelScope)
    }

    // Descubrir películas con filtros
    fun discoverMovies(
        genreId: Int? = null,
        year: Int? = null,
        minRating: Double? = null
    ) {
        searchRepository.discoverMovies(genreId, year, minRating)
            .onEach { result ->
                _searchResults.value = result
            }
            .launchIn(viewModelScope)
    }

    // Cargar géneros
    private fun loadGenres() {
        searchRepository.getGenres()
            .onEach { result ->
                _genres.value = result
            }
            .launchIn(viewModelScope)
    }

    // Limpiar resultados
    fun clearResults() {
        _searchResults.value = null
    }
}