package com.globekeeper.uploader.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.globekeeper.uploader.R
import com.globekeeper.uploader.di.ViewModelFactory
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject


class UploaderFragment : DaggerDialogFragment() {
    companion object {
        private const val ARG_URIS = "uris"

        fun newInstance(uris: ArrayList<Uri>): UploaderFragment {
            return UploaderFragment().apply {
                arguments = bundleOf(ARG_URIS to uris)
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Cancel this dialog only by clicking top right [x] icon
        if (savedInstanceState == null) {
            isCancelable = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.uploader_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
