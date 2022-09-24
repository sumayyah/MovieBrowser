package com.sumayyah.moviebrowser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumayyah.moviebrowser.model.Movie
import com.sumayyah.moviebrowser.network.MovieApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel(private val api: MovieApi): ViewModel() {
    private val uiStateInternal = MutableLiveData<UIState>().apply { postValue(UIState.EMPTY)}
    val uiState: LiveData<UIState> = uiStateInternal

    init {
        fetchData()
    }

    // Kick off api fetch via repository
    // Map repository response to UIState
    private fun fetchData() {
        uiStateInternal.postValue(UIState.LOADING)

        viewModelScope.launch {
           withContext(Dispatchers.IO) {
               try {
                   val response = api.getTrending()
                   val list = response.results

                   Timber.log(1, "Sumi got ${list.size} movies")
                   uiStateInternal.postValue(UIState.SUCCESS(response.results))
               } catch (e: Throwable) {
                   Timber.log(1, "Sumi Error $e")
                   uiStateInternal.postValue(UIState.ERROR)
               }
           }

        }
    }

    // Reload data on swipe to refresh
    fun userSwipeAction() {
        fetchData()
    }

    // Define immutable view states
    sealed class UIState {
        object EMPTY: UIState()
        object ERROR: UIState()
        object LOADING: UIState()
        data class SUCCESS(val list: List<Movie>): UIState()
    }
}