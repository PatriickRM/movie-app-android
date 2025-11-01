package com.patrick.movieapp.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.FavoriteResponse
import com.patrick.movieapp.data.remote.dto.FavoritesStatsResponse
import com.patrick.movieapp.data.remote.dto.PageResponse
import com.patrick.movieapp.data.repository.FavoriteRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<Resource<PageResponse<FavoriteResponse>>>()
    val favorites: LiveData<Resource<PageResponse<FavoriteResponse>>> = _favorites

    private val _favoriteStats = MutableLiveData<Resource<FavoritesStatsResponse>>()
    val favoriteStats: LiveData<Resource<FavoritesStatsResponse>> = _favoriteStats

    private val _addFavoriteResult = MutableLiveData<Resource<FavoriteResponse>>()
    val addFavoriteResult: LiveData<Resource<FavoriteResponse>> = _addFavoriteResult

    private val _removeFavoriteResult = MutableLiveData<Resource<Unit>>()
    val removeFavoriteResult: LiveData<Resource<Unit>> = _removeFavoriteResult

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    init {
        loadFavorites()
        loadFavoriteStats()
    }

    // Cargar favoritos
    fun loadFavorites(page: Int = 0, size: Int = 20) {
        favoriteRepository.getUserFavorites(page, size)
            .onEach { result ->
                _favorites.value = result
            }
            .launchIn(viewModelScope)
    }

    // Cargar stats
    fun loadFavoriteStats() {
        favoriteRepository.getFavoritesStats()
            .onEach { result ->
                _favoriteStats.value = result
            }
            .launchIn(viewModelScope)
    }

    // Agregar favorito
    fun addFavorite(
        movieId: Int,
        movieTitle: String?,
        moviePoster: String?,
        movieOverview: String?,
        releaseDate: String?,
        voteAverage: Double?
    ) {
        favoriteRepository.addFavorite(
            movieId, movieTitle, moviePoster, movieOverview, releaseDate, voteAverage
        )
            .onEach { result ->
                _addFavoriteResult.value = result

                // Si fue exitoso, recargar favoritos y stats
                if (result is Resource.Success) {
                    _isFavorite.value = true
                    loadFavorites()
                    loadFavoriteStats()
                }
            }
            .launchIn(viewModelScope)
    }

    // Eliminar favorito
    fun removeFavorite(movieId: Int) {
        favoriteRepository.removeFavorite(movieId)
            .onEach { result ->
                _removeFavoriteResult.value = result

                // Si fue exitoso, recargar favoritos y stats
                if (result is Resource.Success) {
                    _isFavorite.value = false
                    loadFavorites()
                    loadFavoriteStats()
                }
            }
            .launchIn(viewModelScope)
    }

    // Verificar si es favorito
    fun checkIsFavorite(movieId: Int) {
        favoriteRepository.isFavorite(movieId)
            .onEach { result ->
                if (result is Resource.Success) {
                    _isFavorite.value = result.data ?: false
                }
            }
            .launchIn(viewModelScope)
    }

    // Resetear resultado de agregar
    fun resetAddFavoriteResult() {
        _addFavoriteResult.value = null
    }

    // Resetear resultado de eliminar
    fun resetRemoveFavoriteResult() {
        _removeFavoriteResult.value = null
    }
}