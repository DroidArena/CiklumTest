package com.globekeeper.uploader.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.core.os.bundleOf
import com.globekeeper.uploader.Constants
import com.globekeeper.uploader.R
import dagger.android.support.DaggerAppCompatActivity
import java.util.ArrayList

class MainActivity : DaggerAppCompatActivity(), AlertFragment.FileChooser {
    companion object {
        private const val PICK_FILE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            showFileChooser()
        }
    }

    override fun showFileChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_files)),
            PICK_FILE_REQUEST
        )
    }

    override fun close() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_FILE_REQUEST -> {
                if (requestCode == Activity.RESULT_OK && data != null) {
                    val uris = data.clipData?.let { clipData ->
                        val list = arrayListOf<Uri>()
                        for (i in 0 until clipData.itemCount) {
                            list.add(clipData.getItemAt(i).uri)
                        }
                        if (list.isEmpty()) {
                            showAlert(getString(R.string.nothing_is_chosed))
                            return
                        }
                        list
                    } ?: if (data.data != null) {
                        arrayListOf(data.data)
                    } else {
                        showAlert(getString(R.string.nothing_is_chosed))
                        return
                    }
                    processUrisAndShowUploader(uris)
                } else {
                    showAlert(getString(R.string.nothing_is_chosed))
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun processUrisAndShowUploader(uris: ArrayList<Uri>) {
        if (uris.size > Constants.MAX_FILES) {
            showAlert(resources.getQuantityString(
                R.plurals.you_cannot_select_more_than,
                Constants.MAX_FILES,
                Constants.MAX_FILES
            ))
            return
        }
        val sizes = uris.mapNotNull { uri ->
            contentResolver.query(uri, null, null, null, null)
                ?.use { c ->
                    if (c.moveToFirst()) {
                        val sizeIndex = c.getColumnIndex(OpenableColumns.SIZE)
                        if (!c.isNull(sizeIndex)) c.getLong(sizeIndex) else null
                    } else {
                        null
                    }
                }
        }
        if (sizes.size < uris.size) {
            showAlert(getString(R.string.unable_to_check_file_size))
            return
        }
        val maxFileSizeBytes = Constants.MAX_TOTAL_SIZE_MB * 1024 * 1024
        if (sizes.any { it > maxFileSizeBytes }) {
            showAlert(getString(R.string.max_file_size_is, Constants.MAX_TOTAL_SIZE_MB))
            return
        }
        UploaderFragment.newInstance(uris).show(supportFragmentManager, "uploader")
    }

    private fun showAlert(error: String) {
        AlertFragment.newInstance(error).show(supportFragmentManager, "alert")
    }
}
