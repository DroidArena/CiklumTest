package com.globekeeper.uploader.utils

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import kotlin.math.max
import kotlin.math.min


class InputStreamRequestBody(
    private val uri: Uri,
    private val length: Long,
    private val contentResolver: ContentResolver,
    var progressListener: Listener? = null
) : RequestBody() {
    companion object {
        private const val BUFFER_SIZE = 8192L
    }

    private val contentType =
        contentResolver.getType(uri)?.toMediaType() ?: "application/octet-stream".toMediaType()

    override fun contentType(): MediaType? {
        return contentType
    }

    override fun contentLength(): Long {
        return length
    }

    override fun writeTo(sink: BufferedSink) {
        contentResolver.openInputStream(uri)?.source()?.use { source ->
            if (progressListener != null) {
                var remaining: Long = length
                var size = min(BUFFER_SIZE, remaining)

                while (remaining > 0) {
                    sink.write(source, size)

                    remaining = max(0, remaining - size)
                    size = min(BUFFER_SIZE, remaining)

                    if (progressListener?.onRequestProgress(length - remaining, length) == false) {
                        break
                    }
                }
            } else {
                sink.writeAll(source)
            }
        } ?: throw IllegalStateException("Cannot open stream for uri $uri")
    }

    interface Listener {
        fun onRequestProgress(bytesWritten: Long, contentLength: Long): Boolean
    }
}