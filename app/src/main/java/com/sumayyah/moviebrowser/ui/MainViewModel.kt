package com.sumayyah.moviebrowser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel: ViewModel() {
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
            delay(3000)
            val newState = UIState.SUCCESS(listOf(1,2,3,4,5,6,7,8,9))

            uiStateInternal.postValue(newState)
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
        data class SUCCESS(val list: List<Int>): UIState()
    }
}