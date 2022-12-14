package com.sumayyah.moviebrowser.network

import com.sumayyah.moviebrowser.model.Configuration
import com.sumayyah.moviebrowser.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/3/trending/movie/week")
    suspend fun getTrending(
        //Note - api key should be obfuscated
        @Query("api_key") apiKey: String = "d0bfa2d663af7a94e515085e33ab9615",
        @Query("page") page: Int? = 1,
    ): Response<MovieResponse>

    @GET("/3/search/movie")
    suspend fun search(
        @Query("api_key") apiKey: String = "d0bfa2d663af7a94e515085e33ab9615",
        @Query("page") page: Int? = 1,
        @Query("language") language: String? = "en",
        @Query("query") query: String
    ): Response<MovieResponse>

    @GET("/3/configuration")
    suspend fun getConfig(
        @Query("api_key") apiKey: String = "d0bfa2d663af7a94e515085e33ab9615",
        ): Configuration
}