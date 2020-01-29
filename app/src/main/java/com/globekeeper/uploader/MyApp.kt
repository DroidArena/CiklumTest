package com.globekeeper.uploader

import com.facebook.stetho.Stetho
import com.globekeeper.uploader.di.AppComponent
import com.globekeeper.uploader.di.DaggerAppComponent
import com.globekeeper.uploader.di.RealAppComponent
import com.globekeeper.uploader.di.RealAppComponentProvider
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApp: DaggerApplication(), RealAppComponentProvider {
    private lateinit var appInjector: AppComponent

    override val component: RealAppComponent
        get() = appInjector

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerAppComponent
            .builder()
            .create(this)
            .build()
        appInjector = component
        return component
    }
}