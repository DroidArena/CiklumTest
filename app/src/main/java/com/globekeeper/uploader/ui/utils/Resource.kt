package com.globekeeper.uploader.ui.utils

import com.globekeeper.uploader.errors.StateThrowable

sealed class Resource<T> {
    //data class Progress<T>(var loading: Boolean) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure<T>(val e: StateThrowable, val data: T?) : Resource<T>()

    companion object {
        //fun <T> loading(isLoading: Boolean): Resource<T> = Progress(isLoading)
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> failure(e: Throwable, data: T? = null): Resource<T> =
            Failure(StateThrowable(e), data)
    }
}