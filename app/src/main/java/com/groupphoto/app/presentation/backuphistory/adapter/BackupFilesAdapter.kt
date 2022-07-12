package com.groupphoto.app.presentation.backuphistory.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.groupphoto.app.R
import com.groupphoto.app.databinding.ItemHistoryListBinding
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.util.extensions.decimalPlaces
import com.groupphoto.app.util.extensions.loadImageWithCorners

class BackupFilesAdapter :
    PagedListAdapter<BackupEntity, BackupFilesAdapter.BackupViewHolder>(BACKUP_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        val binding =
            ItemHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BackupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        currentList?.get(holder.bindingAdapterPosition)?.let { holder.setUpData(it) }
    }

    inner class BackupViewHolder(private val binding: ItemHistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setUpData(entity: BackupEntity) {
            binding.ivPhoto.loadImageWithCorners(entity.path, 12)
            binding.tvPhotoTitle.text = entity.fileName
            binding.tvStatus.text = itemView.context.getString(getUploadStatus(entity.uploadStatus))
            binding.tvProgressStatus.text =
                "${entity.uploadPercentage} %"
            binding.tvFileSize.text =
                "${entity.fileSize.decimalPlaces(2)} MB"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.pbUploadingImage.setProgress(entity.uploadPercentage, true)
            } else {
                binding.pbUploadingImage.progress = entity.uploadPercentage
            }
        }

        private fun getUploadStatus(type: Int): Int {
            return when (type) {
                0 -> {
                    R.string.Active
                }
                1 -> {
                    R.string.Completed
                }
                2 -> {
                    R.string.Failed
                }
                else -> R.string.Unknown
            }
        }
    }

    companion object {
        val BACKUP_COMPARATOR = object : DiffUtil.ItemCallback<BackupEntity>() {
            override fun areItemsTheSame(oldItem: BackupEntity, newItem: BackupEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BackupEntity, newItem: BackupEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}