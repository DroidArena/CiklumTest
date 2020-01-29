package com.globekeeper.uploader.domain

import android.net.Uri
import androidx.work.WorkManager
import com.globekeeper.uploader.data.FileRepository
import com.globekeeper.uploader.data.database.AppDatabase
import com.globekeeper.uploader.data.database.entities.UploadEntity
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import com.globekeeper.uploader.workers.UploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class UploadInteractorImpl(private val workManager: WorkManager,
                           private val fileRepository: FileRepository,
                           private val database: AppDatabase):
    UploadInteractor {
    override suspend fun upload(uuid: UUID, uri: Uri, name: String, size: Long) =
        fileRepository.upload(uri, name, size)

    override suspend fun createUploadInfos(uris: List<Uri>): List<UploadInfoDomainModel> {
        return fileRepository.getFilesInfo(uris)
            .map {
                UploadInfoDomainModel(it.uri, it.name, it.size)
            }
    }

    override suspend fun save(uploadInfos: List<Pair<UUID, UploadInfoDomainModel>>) {
        withContext(Dispatchers.IO) {
            database.uploadDao().deleteAll()
            database.uploadDao().insertAll(*uploadInfos.map { (uuid, uploadInfo) ->
                UploadEntity(
                    uuid.toString(),
                    uploadInfo.uri.toString(),
                    uploadInfo.name,
                    uploadInfo.size
                )
            }.toTypedArray())
        }
    }

    override suspend fun loadAllByIds(uuids: List<UUID>): List<UploadInfoDomainModel> {
        return withContext(Dispatchers.IO) {
            database.uploadDao().loadAllByIds(uuids.map {
                it.toString()
            }.toTypedArray())
            .map {
                UploadInfoDomainModel(
                    Uri.parse(it.uri),
                    it.name,
                    it.size,
                    UUID.fromString(it.uuid))
            }
        }
    }

    override suspend fun remove(uri: Uri) {
        workManager.cancelUniqueWork(uri.toString())

        withContext(Dispatchers.IO) {
            database.uploadDao().deleteByUri(uri.toString())
        }
    }

    override suspend fun removeAll() {
        workManager.cancelAllWorkByTag(UploadWorker.TAG)
        //this cleans completed / failed / cancelled works from internal work manager's database
        workManager.pruneWork()

        withContext(Dispatchers.IO) {
            database.uploadDao().deleteAll()
        }
    }
}