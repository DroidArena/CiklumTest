package com.globekeeper.uploader.domain.models

import java.util.*

data class UploadInfoDomainModel(val uri: String,
                                 val name: String,
                                 val size: Long,
                                 val uuid: UUID? = null)