package com.sumayyah.moviebrowser.network

import com.sumayyah.moviebrowser.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/3/trending/movie/week")
    suspend fun getTrending(
        //TODO api key should be obfuscated
        @Query("api_key") apiKey: String = "d0bfa2d663af7a94e515085e33ab9615",
        @Query("page") page: Int? = 1,
    ): MovieResponse

    @GET("/3/search/movie")
    suspend fun search(
        //TODO api key should be obfuscated
        @Query("api_key") apiKey: String = "d0bfa2d663af7a94e515085e33ab9615",
        @Query("page") page: Int? = 1,
        @Query("language") language: String? = "en",
        @Query("query") query: String
    ): MovieResponse
}