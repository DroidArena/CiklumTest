package com.globekeeper.uploader.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {
    @POST("testUpload")
    @Multipart
    suspend fun uploadFile(@Part filePart: MultipartBody.Part, @Part("data") jsonPart: RequestBody): Response<Unit>
}