package com.globekeeper.uploader.domain

import android.net.Uri
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import kotlinx.coroutines.flow.Flow
import java.util.*

interface UploadInteractor {
    suspend fun upload(uri: Uri, name: String, size: Long): Flow<Int>

    suspend fun scheduleUploadsByUris(uris: List<String>): List<UUID>
    suspend fun scheduleUploads(uploadInfos: List<UploadInfoDomainModel>): List<UUID>

    suspend fun loadAllByUUIDs(uuids: List<UUID>): List<UploadInfoDomainModel>
    suspend fun remove(uri: String)
    suspend fun removeAll()
    fun hasNotCancelledWorkers(): Boolean
}