package com.globekeeper.uploader.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "uploads",
    indices = [Index(
        value = ["uri"],
        unique = true
    )]
)
data class UploadEntity(@PrimaryKey val uuid: String,
                        @ColumnInfo(name = "uri") val uri: String,
                        @ColumnInfo(name = "name") val name: String,
                        @ColumnInfo(name = "size") val size: Long)