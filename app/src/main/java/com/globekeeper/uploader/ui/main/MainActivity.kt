package com.globekeeper.uploader.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import com.globekeeper.uploader.Constants
import com.globekeeper.uploader.R
import com.globekeeper.uploader.di.ViewModelFactory
import com.globekeeper.uploader.domain.models.UploadInfoDomainModel
import com.globekeeper.uploader.ui.upload.UploaderFragment
import com.globekeeper.uploader.ui.utils.Resource
import com.globekeeper.uploader.ui.utils.hasNotCancelledWorkers
import com.globekeeper.uploader.workers.UploadWorker
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), AlertFragment.FileChooser, UploaderFragment.UploaderHost {
    private var selectedUris: List<Uri>? = null

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val PICK_FILE_REQUEST = 1
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        viewModel.uploadInfoEvent.observe(this, Observer {
            when (it) {
                is Resource.Success -> validateAndScheduleUploadInfos(it.data)
                is Resource.Failure -> {
                    Log.e(TAG, "exception occured while trying to get files info ${it.e.message}", it.e.cause)
                }
            }
        })
        viewModel.uploadsScheduledEvent.observe(this, Observer {
            when (it) {
                is Resource.Success -> showUploader()
                is Resource.Failure -> {
                    Log.e(
                        TAG,
                        "exception occured while trying to get files info ${it.e.message}",
                        it.e.cause
                    )
                }
            }
        })
        if (savedInstanceState == null) {
            if (WorkManager.getInstance(this).hasNotCancelledWorkers(UploadWorker.TAG)) {
                showUploader()
            } else {
                //if all workers are complete (or cancelled) then cleanup all workers info and allow user to choose new files
                viewModel.cancelAllUploads()

                showFileChooser()
            }
        }
    }

    private fun validateAndScheduleUploadInfos(uploadInfos: List<UploadInfoDomainModel>) {
        val maxFileSizeBytes = Constants.MAX_TOTAL_SIZE_MB * 1024 * 1024
        if (uploadInfos.any { it.size > maxFileSizeBytes }) {
            showAlert(getString(R.string.max_file_size_is, Constants.MAX_TOTAL_SIZE_MB))
        } else {
            viewModel.scheduleUploads(uploadInfos)
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
                //postpone processing URIs to onResume to avoid fragment transaction exceptions
                selectedUris = if (resultCode == Activity.RESULT_OK && data != null) {
                    data.clipData?.let { clipData ->
                        val list = mutableListOf<Uri>()
                        for (i in 0 until clipData.itemCount) {
                            list.add(clipData.getItemAt(i).uri)
                        }
                        list
                    }?:data.data?.let {
                        listOf(it)
                    }?:emptyList()
                } else {
                    emptyList()
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()

        selectedUris?.let { uris ->
            selectedUris = null

            processSelectedFiles(uris)
        }
    }

    private fun processSelectedFiles(uris: List<Uri>) {
        if (uris.isEmpty()) {
            showAlert(getString(R.string.no_files_are_selected))
            return
        }
        if (uris.size > Constants.MAX_FILES) {
            showAlert(resources.getQuantityString(
                R.plurals.you_cannot_select_more_than,
                Constants.MAX_FILES,
                Constants.MAX_FILES
            ))
            return
        }
        viewModel.createUploadInfos(uris)
    }

    private fun showUploader() {
        supportFragmentManager.commit {
            replace(R.id.container, UploaderFragment.newInstance())
        }
    }

    private fun showAlert(error: String) {
        AlertFragment.newInstance(error).show(supportFragmentManager, "alert")
    }

    override fun onUploadsCleaned() {
        supportFragmentManager.findFragmentById(R.id.container)?.let { fragment ->
            supportFragmentManager.commit {
                remove(fragment)
            }
        }
        showFileChooser()
    }
}
