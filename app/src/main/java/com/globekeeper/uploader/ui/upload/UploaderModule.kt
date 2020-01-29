package com.globekeeper.uploader.ui.upload

import androidx.lifecycle.ViewModel
import com.globekeeper.uploader.di.ViewModelKey
import com.globekeeper.uploader.ui.upload.UploaderViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class UploaderModule {
    @Binds
    @IntoMap
    @ViewModelKey(UploaderViewModel::class)
    abstract fun bindUploaderViewModel(uploaderViewModel: UploaderViewModel): ViewModel
}