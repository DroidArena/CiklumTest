package com.globekeeper.uploader.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adviscent.jra.android.cv.result.SingleLiveEvent
import com.globekeeper.uploader.domain.UploadInteractor
import com.globekeeper.uploader.ui.utils.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val uploadInteractor: UploadInteractor
): ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    private val hasExistedUploadsLiveEvent = SingleLiveEvent<Resource<Boolean>>()
    val hasExistedUploadsEvent: LiveData<Resource<Boolean>>
        get() = hasExistedUploadsLiveEvent

    private val uploadsScheduledLiveEvent = SingleLiveEvent<Resource<Unit>>()
    val uploadsScheduledEvent: LiveData<Resource<Unit>>
        get() = uploadsScheduledLiveEvent

    fun scheduleUploads(uris: List<String>) {
        viewModelScope.launch {
            uploadsScheduledLiveEvent.value = try {
                uploadInteractor.removeAll()
                uploadInteractor.scheduleUploadsByUris(uris)

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

    fun checkForExistedUploads() {
        viewModelScope.launch {
            hasExistedUploadsLiveEvent.value = try {
                Resource.success(uploadInteractor.hasNotCancelledWorkers())
            } catch (e: Exception) {
                Resource.failure(e)
            }
        }
    }
}
