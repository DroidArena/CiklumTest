package com.globekeeper.uploader.data

import android.net.Uri
import com.globekeeper.uploader.data.models.FileInfo
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun upload(uri: Uri, name: String, size: Long): Flow<Int>
    suspend fun getFilesInfo(uris: List<String>): List<FileInfo>
}