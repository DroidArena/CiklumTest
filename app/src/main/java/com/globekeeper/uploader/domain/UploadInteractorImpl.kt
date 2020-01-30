package com.globekeeper.uploader.domain

import android.net.Uri
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.globekeeper.uploader.data.FileRepository
import com.globekeeper.uploader.data.database.AppDatabase
import com.globekeeper.uploader.data.database.entities.UploadEntity
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import com.globekeeper.uploader.errors.UploadMaxSizeException
import com.globekeeper.uploader.errors.UploadsEmptyException
import com.globekeeper.uploader.errors.UploadsMaxCountException
import com.globekeeper.uploader.ui.utils.hasNotCancelledWorkers
import com.globekeeper.uploader.workers.UploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class UploadInteractorImpl(
    private val workManager: WorkManager,
    private val fileRepository: FileRepository,
    private val database: AppDatabase,
    private val maxFiles: Int,
    //MB
    private val maxFileSize: Long
) :
    UploadInteractor {
    override suspend fun upload(uri: Uri, name: String, size: Long) =
        fileRepository.upload(uri, name, size)

    override suspend fun scheduleUploadsByUris(uris: List<String>): List<UUID> {
        if (uris.isEmpty()) {
            throw UploadsEmptyException()
        }
        if (uris.size > maxFiles) {
            throw UploadsMaxCountException(maxFiles)
        }
        val uploadInfos = fileRepository.getFilesInfo(uris)
            .map {
                UploadInfoDomainModel(it.uri, it.name, it.size)
            }
        return scheduleUploads(uploadInfos)
    }

    override suspend fun scheduleUploads(uploadInfos: List<UploadInfoDomainModel>): List<UUID> {
        val maxFileSizeBytes = maxFileSize * 1024 * 1024
        if (uploadInfos.any { it.size > maxFileSizeBytes }) {
            throw UploadMaxSizeException(maxFileSize)
        }
        val infoRequestPairs = uploadInfos.map {
            Pair(it, UploadWorker.makeRequest(it))
        }
        withContext(Dispatchers.IO) {
            database.uploadDao().insertAll(*infoRequestPairs.map { (info, request) ->
                UploadEntity(
                    request.id.toString(),
                    info.uri,
                    info.name,
                    info.size
                )
            }.toTypedArray())
        }
        return infoRequestPairs.map { (info, request) ->
            workManager.enqueueUniqueWork(
                info.uri,
                ExistingWorkPolicy.REPLACE,
                request
            )
            request.id
        }
    }

    override suspend fun loadAllByUUIDs(uuids: List<UUID>): List<UploadInfoDomainModel> {
        return withContext(Dispatchers.IO) {
            database.uploadDao().loadAllByIds(uuids.map { it.toString() }.toTypedArray())
                .map {
                    UploadInfoDomainModel(
                        it.uri,
                        it.name,
                        it.size,
                        UUID.fromString(it.uuid)
                    )
                }
        }
    }

    override suspend fun remove(uri: String) {
        withContext(Dispatchers.IO) {
            database.uploadDao().deleteByUri(uri)

            workManager.cancelUniqueWork(uri)
        }
    }

    override fun hasNotCancelledWorkers(): Boolean {
        return workManager.hasNotCancelledWorkers(UploadWorker.TAG)
    }

    override suspend fun removeAll() {
        withContext(Dispatchers.IO) {
            database.uploadDao().deleteAll()
        }
        workManager.cancelAllWorkByTag(UploadWorker.TAG)
        //this cleans completed / failed / cancelled works from internal work manager's database
        workManager.pruneWork()
    }
}