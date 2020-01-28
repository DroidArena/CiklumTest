package com.globekeeper.uploader.di

import com.globekeeper.uploader.BuildConfig
import com.globekeeper.uploader.data.api.Api
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideFileApi(gson: Gson): Api {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.config_url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}