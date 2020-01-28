package com.globekeeper.uploader.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
object AppModule {
    @Provides
    fun provideContext(app: Application): Context = app.applicationContext
}
