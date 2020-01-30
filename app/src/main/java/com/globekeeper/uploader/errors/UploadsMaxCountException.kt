package com.globekeeper.uploader.errors

class UploadsMaxCountException(val count: Int, message: String? = null): UploadValidationException(message)