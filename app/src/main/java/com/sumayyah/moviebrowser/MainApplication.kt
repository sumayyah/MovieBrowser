package com.sumayyah.moviebrowser

import android.app.Application
import com.sumayyah.moviebrowser.di.AppComponent
import com.sumayyah.moviebrowser.di.AppModule
import com.sumayyah.moviebrowser.di.DaggerAppComponent
import timber.log.Timber

class MainApplication : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        component.inject(this)

        Timber.plant(Timber.DebugTree())
    }
}