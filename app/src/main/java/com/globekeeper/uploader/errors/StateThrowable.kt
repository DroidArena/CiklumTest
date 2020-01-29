package com.globekeeper.uploader.errors

class StateThrowable(cause: Throwable): Throwable(cause) {
    private var processed = false
    fun process(): Boolean {
        return if (!processed) {
            processed = true
            true
        } else {
            false
        }
    }
}