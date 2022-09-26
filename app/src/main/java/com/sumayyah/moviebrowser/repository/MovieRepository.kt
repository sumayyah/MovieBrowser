package com.sumayyah.moviebrowser.repository

import com.sumayyah.moviebrowser.model.Movie
import com.sumayyah.moviebrowser.model.MovieResponse
import com.sumayyah.moviebrowser.network.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

class MovieRepository(private val api: MovieApi) {
    var imageBaseUrlStr: String = ""

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    val trendingMap = mutableMapOf<Int, Movie>()

    init {
        fetchConfigData()
    }

    suspend fun getTrendingMovies() : Flow<Response<MovieResponse>> {
        return flow{
            emit(api.getTrending())
        }.onEach {
            if (it.isSuccessful) {
                updateMemoryCache(it.body()?.results)
            }
        }
    }

    suspend fun getSearchResult(query: String) : Flow<Response<MovieResponse>> {
        return flow{
            emit(api.search(query=query))
        }.debounce(500)
    }

    private fun fetchConfigData() {
        scope.launch {
            try {
                val config = api.getConfig()

                // Set the image config url to the current smallest poster size
                config.imageConfig?.let {
                    imageBaseUrlStr = it.baseUrl + it.posterSizes[0]
                }
            } catch (e: Throwable) {
                // Api call didn't work! Hardcode it for now
                imageBaseUrlStr = "http://image.tmdb.org/t/p/w92"
            }
        }
    }

    private fun updateMemoryCache(list: List<Movie>?) {
        list?.forEach {  movie ->
            if (movie.id != null) {
               trendingMap[movie.id!!] = movie
           }
       }
    }
}