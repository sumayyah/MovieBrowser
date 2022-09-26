package com.sumayyah.moviebrowser.repository

import com.sumayyah.moviebrowser.network.MovieApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieRepository(private val api: MovieApi) {
    var imageBaseUrlStr: String = ""

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    init {

        // Fetch configuration data
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
}