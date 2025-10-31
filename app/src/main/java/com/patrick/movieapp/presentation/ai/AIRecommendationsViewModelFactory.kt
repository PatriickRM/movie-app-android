package com.patrick.movieapp.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.patrick.movieapp.data.repository.AIRecommendationRepository

class AIRecommendationsViewModelFactory(
    private val repository: AIRecommendationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIRecommendationsViewModel::class.java)) {
            return AIRecommendationsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}