package com.globekeeper.uploader.models

data class UploadInfo(val uri: String,
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