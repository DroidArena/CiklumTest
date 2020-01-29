package com.globekeeper.uploader.data.storage

import android.net.Uri
import com.globekeeper.uploader.data.models.FileInfo

interface Storage {
    suspend fun getFileInfos(uris: List<Uri>): List<FileInfo>
}