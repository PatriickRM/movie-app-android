package com.patrick.movieapp.presentation.customlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrick.movieapp.data.remote.dto.CustomListDetailResponse
import com.patrick.movieapp.data.remote.dto.CustomListResponse
import com.patrick.movieapp.data.remote.dto.PageResponse
import com.patrick.movieapp.data.repository.CustomListRepository
import com.patrick.movieapp.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CustomListViewModel(
    private val customListRepository: CustomListRepository
) : ViewModel() {
    private val _userLists = MutableLiveData<Resource<PageResponse<CustomListResponse>>>()
    val userLists: LiveData<Resource<PageResponse<CustomListResponse>>> = _userLists

    private val _listDetails = MutableLiveData<Resource<CustomListDetailResponse>>()
    val listDetails: LiveData<Resource<CustomListDetailResponse>> = _listDetails

    private val _createListResult = MutableLiveData<Resource<CustomListResponse>>()
    val createListResult: LiveData<Resource<CustomListResponse>> = _createListResult

    private val _updateListResult = MutableLiveData<Resource<CustomListResponse>>()
    val updateListResult: LiveData<Resource<CustomListResponse>> = _updateListResult

    private val _deleteListResult = MutableLiveData<Resource<Unit>>()
    val deleteListResult: LiveData<Resource<Unit>> = _deleteListResult

    private val _addMovieResult = MutableLiveData<Resource<Unit>>()
    val addMovieResult: LiveData<Resource<Unit>> = _addMovieResult

    private val _removeMovieResult = MutableLiveData<Resource<Unit>>()
    val removeMovieResult: LiveData<Resource<Unit>> = _removeMovieResult

    private val _publicLists = MutableLiveData<Resource<PageResponse<CustomListResponse>>>()
    val publicLists: LiveData<Resource<PageResponse<CustomListResponse>>> = _publicLists

    init {
        loadUserLists()
    }

    // Cargar listas del usuario
    fun loadUserLists(page: Int = 0, size: Int = 20) {
        customListRepository.getUserLists(page, size)
            .onEach { result ->
                _userLists.value = result
            }
            .launchIn(viewModelScope)
    }

    // Cargar detalles de lista
    fun loadListDetails(listId: Long) {
        customListRepository.getListDetails(listId)
            .onEach { result ->
                _listDetails.value = result
            }
            .launchIn(viewModelScope)
    }

    // Crear lista
    fun createList(name: String, description: String?, isPublic: Boolean) {
        customListRepository.createList(name, description, isPublic)
            .onEach { result ->
                _createListResult.value = result

                // Si fue exitoso, recargar listas
                if (result is Resource.Success) {
                    loadUserLists()
                }
            }
            .launchIn(viewModelScope)
    }

    // Actualizar lista
    fun updateList(
        listId: Long,
        name: String?,
        description: String?,
        isPublic: Boolean?
    ) {
        customListRepository.updateList(listId, name, description, isPublic)
            .onEach { result ->
                _updateListResult.value = result

                // Si fue exitoso, recargar detalles
                if (result is Resource.Success) {
                    loadListDetails(listId)
                    loadUserLists()
                }
            }
            .launchIn(viewModelScope)
    }

    // Eliminar lista
    fun deleteList(listId: Long) {
        customListRepository.deleteList(listId)
            .onEach { result ->
                _deleteListResult.value = result

                // Si fue exitoso, recargar listas
                if (result is Resource.Success) {
                    loadUserLists()
                }
            }
            .launchIn(viewModelScope)
    }

    // Agregar película a lista
    fun addMovieToList(
        listId: Long,
        movieId: Int,
        movieTitle: String?,
        moviePoster: String?
    ) {
        customListRepository.addMovieToList(listId, movieId, movieTitle, moviePoster)
            .onEach { result ->
                _addMovieResult.value = result

                // Si fue exitoso, recargar detalles
                if (result is Resource.Success) {
                    loadListDetails(listId)
                }
            }
            .launchIn(viewModelScope)
    }

    // Eliminar película de lista
    fun removeMovieFromList(listId: Long, movieId: Int) {
        customListRepository.removeMovieFromList(listId, movieId)
            .onEach { result ->
                _removeMovieResult.value = result

                // Si fue exitoso, recargar detalles
                if (result is Resource.Success) {
                    loadListDetails(listId)
                }
            }
            .launchIn(viewModelScope)
    }

    // Cargar listas públicas
    fun loadPublicLists(page: Int = 0, size: Int = 20) {
        customListRepository.getPublicLists(page, size)
            .onEach { result ->
                _publicLists.value = result
            }
            .launchIn(viewModelScope)
    }

    // Reset results
    fun resetCreateListResult() {
        _createListResult.value = null
    }

    fun resetUpdateListResult() {
        _updateListResult.value = null
    }

    fun resetDeleteListResult() {
        _deleteListResult.value = null
    }

    fun resetAddMovieResult() {
        _addMovieResult.value = null
    }

    fun resetRemoveMovieResult() {
        _removeMovieResult.value = null
    }
}