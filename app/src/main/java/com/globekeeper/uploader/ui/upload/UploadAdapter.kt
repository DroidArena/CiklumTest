package com.globekeeper.uploader.ui.upload

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.globekeeper.uploader.R
import com.globekeeper.uploader.models.UploadInfo
import kotlinx.android.synthetic.main.item_file.view.*

class UploadAdapter(private val listener: ItemClickListener): ListAdapter<UploadInfo, UploadAdapter.ViewHolder>(DiffCallback()) {
    interface ItemClickListener {
        fun onRetry(item: UploadInfo)
        fun onRemove(item: UploadInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val filename = view.filename
        private val retry = view.retry
        private val progress = view.progress
        private val status = view.status

        init {
            retry.paintFlags = retry.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            retry.setOnClickListener {
                if (adapterPosition >= 0) {
                    listener.onRetry(getItem(adapterPosition))
                }
            }
            status.setOnClickListener {
                if (adapterPosition >= 0 && progress.isVisible) {
                    listener.onRemove(getItem(adapterPosition))
                }
            }
        }

        fun bind(info: UploadInfo) {
            if (info.state == UploadInfo.State.ACTIVE) {
                progress.progress = info.progress
                progress.isVisible = true
            } else {
                progress.isVisible = false
            }
            retry.isVisible = info.state == UploadInfo.State.FAILED

            filename.text = info.name

            status.setImageResource(when (info.state) {
                UploadInfo.State.ACTIVE -> R.drawable.ic_cancel
                UploadInfo.State.FAILED -> R.drawable.ic_error
                UploadInfo.State.COMPLETE -> R.drawable.ic_complete
            })
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<UploadInfo>() {
        override fun areItemsTheSame(oldItem: UploadInfo, newItem: UploadInfo): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: UploadInfo, newItem: UploadInfo): Boolean {
            return oldItem == newItem
        }
    }
}