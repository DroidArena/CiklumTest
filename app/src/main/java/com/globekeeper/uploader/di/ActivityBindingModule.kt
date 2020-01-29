package com.globekeeper.uploader.di

import com.globekeeper.uploader.ui.main.MainActivity
import com.globekeeper.uploader.ui.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        MainModule::class])
    internal abstract fun mainActivity(): MainActivity
}