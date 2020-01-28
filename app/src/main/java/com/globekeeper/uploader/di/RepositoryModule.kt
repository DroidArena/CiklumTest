package com.globekeeper.uploader.di

import android.content.Context
import com.globekeeper.uploader.data.FileRepository
import com.globekeeper.uploader.data.FileRepositoryImpl
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module(includes = [NetworkModule::class])
object RepositoryModule {
    @Provides
    fun provideFilesRepository(context: Context): FileRepository {
        return FileRepositoryImpl(context)
    }
}