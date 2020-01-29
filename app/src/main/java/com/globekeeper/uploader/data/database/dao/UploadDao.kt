package com.globekeeper.uploader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.globekeeper.uploader.data.database.entities.UploadEntity

@Dao
interface UploadDao {
    @Query("SELECT * FROM uploads WHERE uuid IN (:uuids)")
    suspend fun loadAllByIds(uuids: Array<String>): List<UploadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg uploads: UploadEntity)

    @Query("DELETE FROM uploads")
    suspend fun deleteAll()

    @Query("DELETE FROM uploads WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)
}