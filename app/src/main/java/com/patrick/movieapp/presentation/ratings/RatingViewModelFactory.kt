package com.patrick.movieapp.presentation.ratings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.patrick.movieapp.data.repository.RatingRepository

class RatingViewModelFactory(
    private val ratingRepository: RatingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            return RatingViewModel(ratingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}