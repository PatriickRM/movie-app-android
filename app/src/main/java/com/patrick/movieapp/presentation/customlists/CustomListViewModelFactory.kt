package com.patrick.movieapp.presentation.customlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.patrick.movieapp.data.repository.CustomListRepository

class CustomListViewModelFactory(
    private val customListRepository: CustomListRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomListViewModel::class.java)) {
            return CustomListViewModel(customListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}