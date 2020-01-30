package com.globekeeper.uploader.data.storage

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.globekeeper.uploader.data.models.FileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageImpl(private val contentResolver: ContentResolver): Storage {
    override suspend fun getFileInfos(uris: List<String>): List<FileInfo> {
        return withContext(Dispatchers.IO) {
            uris.mapNotNull { uriStr ->
                val uri = Uri.parse(uriStr)
                contentResolver.query(uri, null, null, null, null)
                    ?.use { c ->
                        if (!c.moveToFirst()) return@use null

                        val sizeIndex = c.getColumnIndex(OpenableColumns.SIZE)
                        if (c.isNull(sizeIndex)) return@use null

                        val displayNameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        var filename = if (!c.isNull(displayNameIndex)) c.getString(displayNameIndex) else null
                        //see OpenableColumns.DISPLAY_NAME description
                        if (filename.isNullOrEmpty()) {
                            filename = uri.lastPathSegment
                        }
                        if (filename == null) return@use null

                        FileInfo(
                            uriStr,
                            filename,
                            c.getLong(sizeIndex)
                        )
                    }
            }
        }

    }
}