package com.sumayyah.moviebrowser.di

import com.sumayyah.moviebrowser.MainActivity
import com.sumayyah.moviebrowser.MainApplication
import com.sumayyah.moviebrowser.ui.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(application: MainApplication)
    fun inject(mainFragment: MainFragment)
}