package com.globekeeper.uploader.data.storage

import com.globekeeper.uploader.data.models.FileInfo

interface Storage {
    suspend fun getFileInfos(uris: List<String>): List<FileInfo>
}