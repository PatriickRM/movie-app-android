package com.patrick.movieapp.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.ChangePasswordRequest
import com.patrick.movieapp.data.remote.dto.UpdateProfileRequest
import com.patrick.movieapp.data.remote.dto.UserResponse
import com.patrick.movieapp.data.remote.dto.UserStatsResponse
import com.patrick.movieapp.data.repository.AuthRepository
import com.patrick.movieapp.data.repository.UserRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<Resource<UserResponse>>()
    val user: LiveData<Resource<UserResponse>> = _user

    private val _stats = MutableLiveData<Resource<UserStatsResponse>>()
    val stats: LiveData<Resource<UserStatsResponse>> = _stats

    private val _updateResult = MutableLiveData<Resource<UserResponse>>()
    val updateResult: LiveData<Resource<UserResponse>> = _updateResult

    private val _passwordResult = MutableLiveData<Resource<Unit>>()
    val passwordResult: LiveData<Resource<Unit>> = _passwordResult

    init {
        loadUserData()
    }

    private fun loadUserData() {
        loadUser()
        loadStats()
    }

    private fun loadUser() {
        userRepository.getCurrentUser()
            .onEach { result ->
                _user.value = result
            }
            .launchIn(viewModelScope)
    }

    private fun loadStats() {
        userRepository.getUserStats()
            .onEach { result ->
                _stats.value = result
            }
            .launchIn(viewModelScope)
    }

    fun updateProfile(fullName: String) {
        val request = UpdateProfileRequest(fullName)
        userRepository.updateProfile(request)
            .onEach { result ->
                _updateResult.value = result
                if (result is Resource.Success) {
                    loadUser() // Recargar usuario
                }
            }
            .launchIn(viewModelScope)
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val request = ChangePasswordRequest(currentPassword, newPassword)
        userRepository.changePassword(request)
            .onEach { result ->
                _passwordResult.value = result
            }
            .launchIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun resetUpdateResult() {
        _updateResult.value = null
    }

    fun resetPasswordResult() {
        _passwordResult.value = null
    }
}