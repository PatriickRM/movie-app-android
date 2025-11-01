package com.patrick.movieapp.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.AuthResponse
import com.patrick.movieapp.data.repository.AuthRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableLiveData<Resource<AuthResponse>>()
    val authState: LiveData<Resource<AuthResponse>> = _authState

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init { checkLoginStatus() }

    // Login
    fun login(email: String, password: String) {
        authRepository.login(email, password)
            .onEach { result ->
                _authState.value = result
            }
            .launchIn(viewModelScope)
    }

    // Register
    fun register(email: String, password: String, fullName: String?) {
        authRepository.register(email, password, fullName)
            .onEach { result ->
                _authState.value = result
            }
            .launchIn(viewModelScope)
    }

    // Desconectarse
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }
    // Checkear login status
    private fun checkLoginStatus() {
        authRepository.isLoggedIn()
            .onEach { loggedIn ->
                _isLoggedIn.value = loggedIn }
            .launchIn(viewModelScope)
    }
}
