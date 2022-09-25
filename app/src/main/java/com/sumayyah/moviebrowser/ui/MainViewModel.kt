package com.sumayyah.moviebrowser.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumayyah.moviebrowser.model.Movie
import com.sumayyah.moviebrowser.network.MovieApi
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

class MainViewModel(private val api: MovieApi): ViewModel() {
    private val uiStateInternal = MutableLiveData<UIState>().apply { postValue(UIState.EMPTY)}
    val uiState: LiveData<UIState> = uiStateInternal

    private val trendingMap = mutableMapOf<Int, Movie>()

    private var currentJob: Job = Job()

    private var currentQuery = ""

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
                   uiStateInternal.postValue(UIState.SUCCESS(response.results))

                   //Save items in map
                   response.results.forEach {  movie ->
                       if (movie.id != null) {
                           trendingMap[movie.id!!] = movie
                       }
                   }
               } catch (e: Throwable) {
                   uiStateInternal.postValue(UIState.ERROR)
               }
           }

        }
    }

    // Reload data on swipe to refresh
    fun userSwipeAction() {
        fetchData()
    }

    // Fire search request
    fun newSearchInput(query: String?) {
        executeSearch(query)
    }

    fun searchClosed() {
        uiStateInternal.value = UIState.SUCCESS(trendingMap.values.toList())
    }

    private fun executeSearch(query: String?) {
        if (query.isNullOrEmpty()) return
        if (query == currentQuery) return

        currentQuery = query

        currentJob.cancel()
        currentJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(1000)
                try {
                    val response = api.search(query=query)
                    uiStateInternal.postValue(UIState.SUCCESS(response.results))

                } catch (e: Exception) {
                    //TODO more specific search related error
                    uiStateInternal.postValue(UIState.ERROR)
                }
            }
        }
    }

    // Define immutable view states
    sealed class UIState {
        object EMPTY: UIState()
        object ERROR: UIState()
        object LOADING: UIState()
        data class SUCCESS(val list: List<Movie>): UIState()
    }
}