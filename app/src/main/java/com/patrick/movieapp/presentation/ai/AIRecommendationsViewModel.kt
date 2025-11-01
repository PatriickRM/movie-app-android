package com.patrick.movieapp.presentation.ai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.ai.request.AIRecommendationRequest
import com.patrick.movieapp.data.remote.dto.ai.response.*

import com.patrick.movieapp.data.repository.AIRecommendationRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AIRecommendationsViewModel(
    private val repository: AIRecommendationRepository
) : ViewModel() {

    private val _recommendations = MutableLiveData<Resource<AIRecommendationResponse>>()
    val recommendations: LiveData<Resource<AIRecommendationResponse>> = _recommendations

    private val _aiLimit = MutableLiveData<Resource<AILimitResponse>>()
    val aiLimit: LiveData<Resource<AILimitResponse>> = _aiLimit

    fun getRecommendations(prompt: String, includeUserHistory: Boolean = true) {
        val request = AIRecommendationRequest(
            prompt = prompt,
            includeUserHistory = includeUserHistory,
            maxRecommendations = 5
        )

        repository.getRecommendations(request)
            .onEach { result ->
                _recommendations.value = result
            }
            .launchIn(viewModelScope)
    }

    fun checkAILimit() {
        repository.checkAILimit()
            .onEach { result ->
                _aiLimit.value = result
            }
            .launchIn(viewModelScope)
    }
}