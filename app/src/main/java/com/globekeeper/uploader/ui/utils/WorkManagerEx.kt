package com.globekeeper.uploader.ui.utils

import androidx.work.WorkInfo
import androidx.work.WorkManager

fun WorkManager.hasNotCancelledWorkers(tag: String): Boolean {
    return getWorkInfosByTag(tag)
        .get()?.let { infos ->
            infos.any { it.state != WorkInfo.State.CANCELLED }
        } ?: false
}