package com.globekeeper.uploader.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.globekeeper.uploader.data.database.dao.UploadDao
import com.globekeeper.uploader.data.database.entities.UploadEntity

@Database(entities = [UploadEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun uploadDao(): UploadDao
}