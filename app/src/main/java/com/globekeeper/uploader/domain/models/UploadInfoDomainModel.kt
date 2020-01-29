package com.globekeeper.uploader.domain.models

import android.net.Uri
import java.util.*

data class UploadInfoDomainModel(val uri: Uri,
                                 val name: String,
                                 val size: Long,
                                 val uuid: UUID? = null)