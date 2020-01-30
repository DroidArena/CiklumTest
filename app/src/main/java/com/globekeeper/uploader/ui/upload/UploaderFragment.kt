package com.globekeeper.uploader.ui.upload

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.globekeeper.uploader.R
import com.globekeeper.uploader.di.ViewModelFactory
import com.globekeeper.uploader.models.UploadInfo
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.uploader_fragment.*
import javax.inject.Inject


class UploaderFragment : DaggerFragment(), UploadAdapter.ItemClickListener {
    companion object {
        fun newInstance() = UploaderFragment()
    }

    interface UploaderHost {
        fun onUploadsCleaned()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapter: UploadAdapter

    private var uploaderHost: UploaderHost? = null

    private val viewModel by viewModels<UploaderViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.uploader_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UploadAdapter(this)

        files.setHasFixedSize(true)
        files.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        files.adapter = adapter

        clearAll.setOnClickListener {
            viewModel.clearAll()

            uploaderHost?.onUploadsCleaned()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        uploaderHost = context as? UploaderHost
    }

    override fun onDetach() {
        uploaderHost = null

        super.onDetach()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.fileInfoLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                updateUi(it)
            } else {
                uploaderHost?.onUploadsCleaned()
            }
        })
    }

    private fun updateUi(infos: List<UploadInfo>) {
        adapter.submitList(infos)

        val hasActive = infos.any { it.state == UploadInfo.State.ACTIVE }
        if (hasActive) {
            processLabel.setText(R.string.processing_uploads)

            val completeCount = infos.count {
                it.state == UploadInfo.State.COMPLETE
            }
            countProgress.text = getString(R.string.fmt_progress_counter, completeCount, infos.size)
            countProgress.isActivated = false
            countProgress.isVisible = true
        } else {
            processLabel.setText(R.string.upload_complete)

            val failedCount = infos.count {
                it.state == UploadInfo.State.FAILED
            }
            if (failedCount > 0) {
                countProgress.text = getString(R.string.fmt_failed_count, failedCount)
                countProgress.isActivated = true
                countProgress.isVisible = true
            } else {
                countProgress.isVisible = false
            }
        }
    }

    override fun onRetry(item: UploadInfo) {
        viewModel.retry(item)
    }

    override fun onRemove(item: UploadInfo) {
        viewModel.remove(item.uri)
    }
}
