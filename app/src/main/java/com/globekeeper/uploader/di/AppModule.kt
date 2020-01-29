package com.globekeeper.uploader.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.globekeeper.uploader.data.FileRepository
import com.globekeeper.uploader.data.FileRepositoryImpl
import com.globekeeper.uploader.data.api.Api
import com.globekeeper.uploader.data.database.AppDatabase
import com.globekeeper.uploader.data.storage.Storage
import com.globekeeper.uploader.data.storage.StorageImpl
import com.globekeeper.uploader.domain.UploadInteractor
import com.globekeeper.uploader.domain.UploadInteractorImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("unused")
@Module(includes = [NetworkModule::class])
object AppModule {
    @Provides
    fun provideContext(app: Application): Context = app.applicationContext

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideStorage(context: Context): Storage {
        return StorageImpl(context.contentResolver)
    }

    @Singleton
    @Provides
    fun provideFilesRepository(context: Context, storage: Storage, api: Api, gson: Gson): FileRepository {
        return FileRepositoryImpl(context.contentResolver, api, storage, gson)
    }

    @Singleton
    @Provides
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideUploadInteractor(workManager: WorkManager,
                                fileRepository: FileRepository,
                                appDatabase: AppDatabase): UploadInteractor {
        return UploadInteractorImpl(
            workManager,
            fileRepository,
            appDatabase
        )
    }
}
