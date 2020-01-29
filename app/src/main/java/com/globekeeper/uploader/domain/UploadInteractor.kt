package com.globekeeper.uploader.domain

import android.net.Uri
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import kotlinx.coroutines.flow.Flow
import java.util.*

interface UploadInteractor {
    suspend fun upload(uuid: UUID, uri: Uri, name: String, size: Long): Flow<Int>
    suspend fun removeAll()
    suspend fun createUploadInfos(uris: List<Uri>): List<UploadInfoDomainModel>
    suspend fun save(uploadInfos: List<Pair<UUID, UploadInfoDomainModel>>)
    suspend fun loadAllByIds(uuids: List<UUID>): List<UploadInfoDomainModel>
    suspend fun remove(uri: Uri)
}