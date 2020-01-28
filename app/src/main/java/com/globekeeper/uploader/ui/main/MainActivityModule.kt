package com.globekeeper.uploader.ui.main

import androidx.lifecycle.ViewModel
import com.globekeeper.uploader.di.FragmentScoped
import com.globekeeper.uploader.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun mainFragment(): UploaderFragment
}