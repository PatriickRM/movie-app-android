package com.patrick.movieapp.presentation.ratings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.PageResponse
import com.patrick.movieapp.data.remote.dto.RatingResponse
import com.patrick.movieapp.data.repository.RatingRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RatingViewModel(
    private val ratingRepository: RatingRepository) : ViewModel() {
    private val _userRatings = MutableLiveData<Resource<PageResponse<RatingResponse>>>()
    val userRatings: LiveData<Resource<PageResponse<RatingResponse>>> = _userRatings

    private val _movieRating = MutableLiveData<Resource<RatingResponse>>()
    val movieRating: LiveData<Resource<RatingResponse>> = _movieRating

    private val _addRatingResult = MutableLiveData<Resource<RatingResponse>>()
    val addRatingResult: LiveData<Resource<RatingResponse>> = _addRatingResult

    private val _updateRatingResult = MutableLiveData<Resource<RatingResponse>>()
    val updateRatingResult: LiveData<Resource<RatingResponse>> = _updateRatingResult

    private val _deleteRatingResult = MutableLiveData<Resource<Unit>>()
    val deleteRatingResult: LiveData<Resource<Unit>> = _deleteRatingResult

    private val _hasRated = MutableLiveData<Boolean>()
    val hasRated: LiveData<Boolean> = _hasRated

    init {
        loadUserRatings()
    }

    // Cargar ratings del usuario
    fun loadUserRatings(page: Int = 0, size: Int = 20) {
        ratingRepository.getUserRatings(page, size)
            .onEach { result ->
                _userRatings.value = result
            }
            .launchIn(viewModelScope)
    }

    // Cargar rating de película específica
    fun loadMovieRating(movieId: Int) {
        ratingRepository.getRatingByMovie(movieId)
            .onEach { result ->
                _movieRating.value = result
            }
            .launchIn(viewModelScope)
    }

    // Agregar o actualizar rating
    fun addOrUpdateRating(movieId: Int, rating: Double, review: String?) {
        ratingRepository.addOrUpdateRating(movieId, rating, review)
            .onEach { result ->
                _addRatingResult.value = result

                // Si fue exitoso, actualizar estado
                if (result is Resource.Success) {
                    _hasRated.value = true
                    loadUserRatings()
                }
            }
            .launchIn(viewModelScope)
    }

    // Actualizar rating existente
    fun updateRating(movieId: Int, rating: Double, review: String?) {
        ratingRepository.updateRating(movieId, rating, review)
            .onEach { result ->
                _updateRatingResult.value = result

                // Si fue exitoso, recargar
                if (result is Resource.Success) {
                    loadMovieRating(movieId)
                    loadUserRatings()
                }
            }
            .launchIn(viewModelScope)
    }

    // Eliminar rating
    fun deleteRating(movieId: Int) {
        ratingRepository.deleteRating(movieId)
            .onEach { result ->
                _deleteRatingResult.value = result

                // Si fue exitoso, actualizar estado
                if (result is Resource.Success) {
                    _hasRated.value = false
                    loadUserRatings()
                }
            }
            .launchIn(viewModelScope)
    }

    // Verificar si ya calificó
    fun checkHasRated(movieId: Int) {
        ratingRepository.hasRated(movieId)
            .onEach { result ->
                if (result is Resource.Success) {
                    _hasRated.value = result.data ?: false
                }
            }
            .launchIn(viewModelScope)
    }

    // Reset results
    fun resetAddRatingResult() {
        _addRatingResult.value = null
    }

    fun resetUpdateRatingResult() {
        _updateRatingResult.value = null
    }

    fun resetDeleteRatingResult() {
        _deleteRatingResult.value = null
    }
}