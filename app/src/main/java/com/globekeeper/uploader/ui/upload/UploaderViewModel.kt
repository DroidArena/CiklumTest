package com.globekeeper.uploader.ui.upload

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.globekeeper.uploader.domain.UploadInteractor
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import com.globekeeper.uploader.models.UploadInfo
import com.globekeeper.uploader.workers.UploadWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploaderViewModel @Inject constructor(
    private val uploadInteractor: UploadInteractor,
    private val context: Context
) : ViewModel() {
    companion object {
        private val TAG = UploaderViewModel::class.java.simpleName
    }

    /**
     * Provides uploads list for UI
     *
     * [UploadInteractor.loadAllByIds] gets upload info from database and
     * only assists to info from work manager. This assistant needed because
     * [UploadWorker] input params are not available from [WorkInfo],
     * at least until work is started, then we can set data in workInfo.progress,
     * but worker could be in just queued state for some time
     */
    val fileInfoLiveData = WorkManager.getInstance(context)
        .getWorkInfosByTagLiveData(UploadWorker.TAG)
        .switchMap { workInfos ->
            val nonCancelledWorkInfos = workInfos.filter {
                it.state != WorkInfo.State.CANCELLED
            }
            liveData(viewModelScope.coroutineContext) {
                val nonCancelledWorkInfosIds = nonCancelledWorkInfos.map { it.id }
                val uploadDomainModels = uploadInteractor.loadAllByIds(nonCancelledWorkInfosIds)
                val uploadInfos = uploadDomainModels.mapNotNull { info ->
                    val workInfo = nonCancelledWorkInfos.firstOrNull { it.id == info.uuid }
                        ?: return@mapNotNull null

                    UploadInfo(
                        info.uri,
                        info.name,
                        info.size,
                        when (workInfo.state) {
                            WorkInfo.State.SUCCEEDED -> UploadInfo.State.COMPLETE
                            WorkInfo.State.FAILED -> UploadInfo.State.FAILED
                            else -> UploadInfo.State.ACTIVE
                        },
                        workInfo.progress.getInt(UploadWorker.ARG_PROGRESS, 0),
                        workInfo.outputData.getInt(UploadWorker.ARG_ERROR_CODE, 0)
                    )
                }
                emit(uploadInfos)
            }
        }

    fun retry(item: UploadInfo) {
        UploadWorker.scheduleJob(context, UploadInfoDomainModel(item.uri, item.name, item.size))
    }

    fun remove(uri: Uri) {
        viewModelScope.launch {
            try {
                uploadInteractor.remove(uri)
            } catch (e: Exception) {
                Log.e(TAG, "fail to remove upload record ${e.message}", e)
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            try {
                uploadInteractor.removeAll()
            } catch (e: Exception) {
                Log.e(TAG, "fail to clean upload records ${e.message}", e)
            }
        }
    }
}
