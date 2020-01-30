package com.globekeeper.uploader.errors

class UploadMaxSizeException(val maxSize: Long, message: String? = null): UploadValidationException(message)