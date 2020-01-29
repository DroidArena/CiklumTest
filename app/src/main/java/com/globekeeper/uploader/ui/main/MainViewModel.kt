package com.globekeeper.uploader.ui.main

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.adviscent.jra.android.cv.result.SingleLiveEvent
import com.globekeeper.uploader.domain.UploadInteractor
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import com.globekeeper.uploader.ui.utils.Resource
import com.globekeeper.uploader.workers.UploadWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val context: Context,
    private val uploadInteractor: UploadInteractor
): ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    private val uploadInfoLiveEvent = SingleLiveEvent<Resource<List<UploadInfoDomainModel>>>()
    val uploadInfoEvent: LiveData<Resource<List<UploadInfoDomainModel>>>
        get() = uploadInfoLiveEvent

    private val uploadsScheduledLiveEvent = SingleLiveEvent<Resource<Unit>>()
    val uploadsScheduledEvent: LiveData<Resource<Unit>>
        get() = uploadsScheduledLiveEvent

    fun createUploadInfos(uris: List<Uri>) {
        viewModelScope.launch {
            uploadInfoLiveEvent.value = try {
                Resource.success(uploadInteractor.createUploadInfos(uris))
            } catch (e: Exception) {
                Resource.failure(e)
            }
        }
    }

    fun scheduleUploads(uploadInfos: List<UploadInfoDomainModel>) {
        //clean work manager internal database
        WorkManager.getInstance(context).pruneWork()

        //start uploads
        viewModelScope.launch {
            uploadsScheduledLiveEvent.value = try {
                uploadInteractor.save(uploadInfos.map { info ->
                    val uuid = UploadWorker.scheduleJob(context, info)
                    Pair(uuid, info)
                })
                Resource.success(Unit)
            } catch (e: Exception) {
                Resource.failure(e)
            }
        }
    }

    fun cancelAllUploads() {
        viewModelScope.launch {
            try {
                uploadInteractor.removeAll()
            } catch (e: Exception) {
                Log.e(TAG, "fail to clean upload records ${e.message}", e)
            }
        }
    }
}
