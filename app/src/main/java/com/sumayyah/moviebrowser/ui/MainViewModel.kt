package com.sumayyah.moviebrowser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumayyah.moviebrowser.model.Movie
import com.sumayyah.moviebrowser.repository.MovieRepository
import kotlinx.coroutines.*

/**
 * MainViewModel:
 * - emits UIStates with all data necessary to render the state, and consumes user events
 * - observes data stream emitted by Repository class, then maps it to LiveData for views
 * - scopes all network calls to its lifecycle to prevent memory leaks
 * - maintains in-memory livedata of user data so that configuration change or app background/foreground doesn't reload data
 * - is scoped to the lifecyle of MainFragment instead of Activity (right now only MainFragment needs access to data)
 * */
class MainViewModel(private val repository: MovieRepository): ViewModel() {
    private val uiStateInternal = MutableLiveData<UIState>().apply { postValue(UIState.LOADING)}
    val uiState: LiveData<UIState> = uiStateInternal

    private var currentJob: Job = Job()

    private var currentQuery = ""

    init {
        fetchTrendingData()
    }

    private fun fetchTrendingData() {
        uiStateInternal.postValue(UIState.LOADING)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getTrendingMovies().collect {

                    if (it.isSuccessful) {
                        val response = it.body()
                        val list = response?.results ?: listOf<Movie>()

                        //Construct the image url for each poster
                        list.forEach { movie->
                            movie.gridPosterUrl = repository.imageBaseUrlStr + movie.posterPath
                        }
                        uiStateInternal.postValue(UIState.SUCCESS(list))
                    } else {
                        uiStateInternal.postValue(UIState.ERROR)
                    }
                }
            }
        }
    }

    // Reload data on swipe to refresh
    fun userSwipeAction() {
        fetchTrendingData()
    }

    // Fire search request
    fun newSearchInput(query: String?) {
        executeSearch(query)
    }

    // Fired when user hits X button in search widget - just reload trending
    fun searchClosed() {
        uiStateInternal.value = UIState.SUCCESS(repository.trendingMap.values.toList())
    }

    private fun executeSearch(query: String?) {
        if (query.isNullOrEmpty()) return
        if (query == currentQuery) return

        currentQuery = query

        // Cancel the previous search query before fetching the new one
        currentJob.cancel()

        currentJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getSearchResult(query).collect {
                    if (it.isSuccessful) {
                        val response = it.body()
                        val list = response?.results ?: listOf<Movie>()

                        //Construct the image url for each poster
                        list.forEach { movie->
                            movie.gridPosterUrl = repository.imageBaseUrlStr + movie.posterPath
                        }
                        uiStateInternal.postValue(UIState.SUCCESS(list))
                    } else {
                        uiStateInternal.postValue(UIState.ERROR)
                    }
                }
            }
        }
    }

    // Define immutable view states
    sealed class UIState {
        object ERROR: UIState()
        object LOADING: UIState()
        data class SUCCESS(val list: List<Movie>): UIState()
    }
}