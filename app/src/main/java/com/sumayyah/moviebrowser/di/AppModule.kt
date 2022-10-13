package com.sumayyah.moviebrowser.di

import android.app.Application
import com.sumayyah.moviebrowser.BuildConfig
import com.sumayyah.moviebrowser.network.MovieApi
import com.sumayyah.moviebrowser.repository.MovieRepository
import com.sumayyah.moviebrowser.ui.MainViewModelFactory
import dagger.Module
import dagger.Provides
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cookieJar(JavaNetCookieJar(cookieManager))
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(api: MovieApi): MovieRepository {
        return MovieRepository(api)
    }

    @Provides
    @Singleton
    fun provideMainViewModelFactory(repository: MovieRepository): MainViewModelFactory {
        return MainViewModelFactory(repository)
    }
}