package com.globekeeper.uploader.data.models

import android.net.Uri

data class FileInfo(val uri: Uri,
                    val name: String,
                    val size: Long)