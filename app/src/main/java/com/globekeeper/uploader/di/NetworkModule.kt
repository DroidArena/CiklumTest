package com.globekeeper.uploader.di

import com.globekeeper.uploader.BuildConfig
import com.globekeeper.uploader.Constants
import com.globekeeper.uploader.data.api.Api
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Suppress("unused")
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
       val builder = OkHttpClient.Builder()
           .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
           .readTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
           .writeTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideFileApi(client: OkHttpClient, gson: Gson): Api {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.config_url)
            .client(client)
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