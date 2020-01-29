package com.globekeeper.uploader.ui.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure<T>(val e: Throwable, val data: T?) : Resource<T>()

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> failure(e: Throwable, data: T? = null): Resource<T> =
            Failure(e, data)
    }
}