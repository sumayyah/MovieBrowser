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

/**
 * MovieRepository:
 * - Gets configuration information so that movie objects can construct the image url they need at runtime
 * - Manages the network fetch for movies and search results
 * - Exposes StateFlow of api response for any observers (in this case, MainViewModel)
 * - Maintains in-memory cache (see notes below for future improvements) so that other detail views can access single Movies
 * */
class MovieRepository(private val api: MovieApi) {
    // Note - in the future we can abstract this out into an ImageProvider class, with a getImageBaseUrl() function
    var imageBaseUrlStr: String = ""

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    // Basic in memory cache of Movie results. We can build a whole caching layer instead of this:
    // aka disk caching of any recent data with an LRU purging system, plus an in-memory cache for immediate
    // storage of api responses. We'd probably build a DataSource class or something similar to handle
    // caching logic, syncronization, etc
    val trendingMap = mutableMapOf<Int, Movie>()

    init {
        fetchConfigData()
    }

    private fun fetchConfigData() {
        scope.launch {
            try {
                val config = api.getConfig()

                // Hardcode the image config url to the current smallest poster size
                // In the future, we can store all the sizes in an ImageProvider class and just ask for the url we want
                config.imageConfig?.let {
                    imageBaseUrlStr = it.baseUrl + it.posterSizes[0]
                }
            } catch (e: Throwable) {
                // Api call didn't work! Hardcode it for now
                imageBaseUrlStr = "http://image.tmdb.org/t/p/w92"
            }
        }
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

    // Make sure to build in a small delay so we don't fire too many api calls
    suspend fun getSearchResult(query: String) : Flow<Response<MovieResponse>> {
        return flow{
            emit(api.search(query=query))
        }.debounce(500)
    }

    private fun updateMemoryCache(list: List<Movie>?) {
        list?.forEach {  movie ->
            if (movie.id != null) {
               trendingMap[movie.id!!] = movie
           }
       }
    }
}