package com.globekeeper.uploader.ui.main

import androidx.lifecycle.ViewModel
import com.globekeeper.uploader.di.FragmentScoped
import com.globekeeper.uploader.di.ViewModelKey
import com.globekeeper.uploader.ui.upload.UploaderFragment
import com.globekeeper.uploader.ui.upload.UploaderModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(MainViewModel: MainViewModel): ViewModel

    @FragmentScoped
    @ContributesAndroidInjector(modules = [UploaderModule::class])
    internal abstract fun uploadFragment(): UploaderFragment
}