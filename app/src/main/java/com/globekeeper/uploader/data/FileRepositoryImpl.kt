package com.globekeeper.uploader.data

import android.content.ContentResolver
import android.net.Uri
import com.globekeeper.uploader.data.api.Api
import com.globekeeper.uploader.data.models.FileInfo
import com.globekeeper.uploader.data.storage.Storage
import com.globekeeper.uploader.errors.UploadException
import com.globekeeper.uploader.utils.InputStreamRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class FileRepositoryImpl(private val contentResolver: ContentResolver,
                         private val api: Api,
                         private val storage: Storage,
                         private val gson: Gson
): FileRepository {
    companion object {
        private const val FILE_NAME_FIELD = "upload"
    }

    private data class DataModel(val name: String)
    private data class RequestModel(val data: DataModel)

    @ExperimentalCoroutinesApi
    override suspend fun upload(uri: Uri, name: String, size: Long) = callbackFlow {
        val nameRequestBody = gson.toJson(RequestModel(DataModel(name)))
            .toRequestBody("application/json".toMediaType())

        val fileRequestBody = InputStreamRequestBody(uri, size, contentResolver, object: InputStreamRequestBody.Listener {
            override fun onRequestProgress(
                bytesWritten: Long,
                contentLength: Long
            ): Boolean {
                return if (this@callbackFlow.isActive) {
                    offer((bytesWritten * 100 / contentLength).toInt())
                    true
                } else {
                    false
                }
            }
        })
        val filePart = MultipartBody.Part.createFormData(FILE_NAME_FIELD, name, fileRequestBody)
        try {
            val response = api.uploadFile(
                filePart,
                nameRequestBody
            )
            offer(100)

            if (response.code() != 204) {
                throw UploadException(response.code(), "Unknown server response")
            }
            close()
        } catch (t: Throwable) {
            close(t)
        }
        awaitClose { fileRequestBody.progressListener = null }
    }.flowOn(Dispatchers.IO)
    .buffer(128)


    override suspend fun getFilesInfo(uris: List<String>): List<FileInfo> {
        return storage.getFileInfos(uris)
    }
}