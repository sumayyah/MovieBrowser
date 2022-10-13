package com.sumayyah.moviebrowser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sumayyah.moviebrowser.network.MovieApi
import com.sumayyah.moviebrowser.repository.MovieRepository

class MainViewModelFactory(private val repository: MovieRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}