package com.globekeeper.uploader.models

import android.net.Uri

data class UploadInfo(val uri: Uri,
                      val name: String,
                      val size: Long,
                      val state: State = State.ACTIVE,
                      val progress: Int = 0,
                      val errorCode: Int = 0) {
    enum class State {
        ACTIVE,
        FAILED,
        COMPLETE
    }
}