package com.globekeeper.uploader.di

import com.globekeeper.uploader.workers.UploadWorker

interface RealAppComponent {
    fun inject(uploadWorker: UploadWorker)
}
