package com.globekeeper.uploader.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.globekeeper.uploader.di.RealAppComponentProvider
import com.globekeeper.uploader.domain.UploadInteractor
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import com.globekeeper.uploader.errors.UploadException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import retrofit2.HttpException
import javax.inject.Inject

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override val coroutineContext = Dispatchers.IO

    @Inject
    lateinit var uploadInteractor: UploadInteractor

    init {
        (context.applicationContext as RealAppComponentProvider).component.inject(this)
    }

    override suspend fun doWork(): Result {
        val uriStr = inputData.getString(ARG_URI)
        require(!uriStr.isNullOrEmpty()) { "invalid file URI" }

        val name = inputData.getString(ARG_NAME)
        require(!name.isNullOrEmpty()) { "invalid file name" }

        val size = inputData.getLong(ARG_SIZE, 0)
        require(size > 0) { "file size must be positive number" }

        val uri = Uri.parse(uriStr)

        return try {
            uploadInteractor.upload(uri, name, size)
                .collect { progress ->
                    setProgress(Data.Builder()
                       .putInt(ARG_PROGRESS, progress)
                       .build())
                }
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "upload error: ${throwable.message} for uri $uri", throwable)

            val code = when (throwable) {
                is UploadException -> throwable.code
                //non-2xx HTTP response from retrofit
                is HttpException -> throwable.code()
                else -> 0
            }
            Result.failure(Data.Builder()
                .putInt(ARG_ERROR_CODE, code)
                .build())
        }
    }

    companion object {
        const val TAG = "upload-worker"

        const val ARG_ERROR_CODE = "code"
        const val ARG_PROGRESS = "progress"

        private const val ARG_URI = "uri"
        private const val ARG_NAME = "name"
        private const val ARG_SIZE = "size"

        fun makeRequest(uploadInfo: UploadInfoDomainModel): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UploadWorker>()
                .addTag(TAG)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(ARG_URI, uploadInfo.uri)
                        .putString(ARG_NAME, uploadInfo.name)
                        .putLong(ARG_SIZE, uploadInfo.size)
                        .build()
                )
                .build()
        }
    }
}