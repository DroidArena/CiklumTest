package com.globekeeper.uploader.ui.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.globekeeper.uploader.R

class AlertFragment : DialogFragment() {
    companion object {
        private const val ARG_MESSAGE = "msg"

        fun newInstance(msg: String): AlertFragment {
            return AlertFragment().apply {
                arguments = bundleOf(ARG_MESSAGE to msg)
            }
        }
    }

    interface FileChooser {
        fun showFileChooser()
        fun close()
    }

    private var fileChooser: FileChooser? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fileChooser = context as? FileChooser
    }

    override fun onDetach() {
        super.onDetach()

        fileChooser = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            isCancelable = false
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(arguments?.getString(ARG_MESSAGE))
            .setPositiveButton(R.string.choose) { _, _ ->
                fileChooser?.showFileChooser()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                fileChooser?.close()
            }
            .show()
    }
}