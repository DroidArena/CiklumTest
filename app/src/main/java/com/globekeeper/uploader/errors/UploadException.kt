package com.globekeeper.uploader.errors

class UploadException(val code: Int, message: String, cause: Throwable? = null): RuntimeException(message, cause)