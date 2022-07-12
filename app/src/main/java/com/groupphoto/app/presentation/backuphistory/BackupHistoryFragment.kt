package com.groupphoto.app.presentation.backuphistory

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.groupphoto.app.GroupPhoto
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.dao.BackupRoomDatabase
import com.groupphoto.app.databinding.FragmentBackupHistoryBinding
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.presentation.backuphistory.adapter.BackUpFilesViewPagerAdapter
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.worker.UploadWorker

class BackupHistoryFragment :
    BaseFragment<FragmentBackupHistoryBinding>(FragmentBackupHistoryBinding::inflate) {

    var historyEntity: List<BackupEntity> = ArrayList()
    private lateinit var adapter: BackUpFilesViewPagerAdapter
    private var tabTitles = listOf<Int>(
        R.string.Active,
        R.string.Completed,
        R.string.Failed
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = BackUpFilesViewPagerAdapter(parentFragmentManager, lifecycle)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.vpBackUpHistory.adapter = adapter
        TabLayoutMediator(binding.tlBackUpHistoryStatus, binding.vpBackUpHistory) { tab, position ->
            tab.setText(tabTitles[position])
        }.attach()

        GroupPhoto.uploadingImageFileCounter.observe(viewLifecycleOwner, Observer {
            val fileSize = GroupPhoto.uploadingImageFiles.value?.size ?: 1
            if (fileSize != 0) {
                showUiOfProgress(it + 1, fileSize)
            }
        })

        GroupPhoto.uploadingImageFiles.observe(viewLifecycleOwner, Observer {
            GroupPhoto.uploadingImageFileCounter.value = GroupPhoto.uploadingImageFileCounter.value
        })

        binding.llBackUpState.setOnClickListener {
            SharedPref.backupPaused = !SharedPref.backupPaused
            changeBackUpState(SharedPref.backupPaused)
        }
        changeBackUpState(!SharedPref.backupPaused)

    }

    private fun showUiOfProgress(counter: Int, uploadingFileSize: Int) {
        binding.tvFileSize.setText(
            "Uploading" +
                    counter + "/" +
                    uploadingFileSize.toString() + " items (2 GB)"
        )
        val progress = (counter / uploadingFileSize) * 100
        binding.pbUploadingImage.progress = progress
        binding.tvPercentage.setText("$progress %")
    }

    private fun changeBackUpState(isUploading: Boolean = false) {
        if (isUploading) {
            binding.tvBackupStatus.text = getString(R.string.Paused)
            binding.tvBackUpState.text = getString(R.string.PauseUpload)
            binding.llBackUpState.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_light_link_4dp)
            binding.ivBackupState.setImageResource(R.drawable.ic_pause)
            binding.tvBackUpState.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )
        } else {
            binding.tvBackupStatus.text = getString(R.string.Enabled)
            binding.tvBackUpState.text = getString(R.string.StartUpload)
            binding.ivBackupState.setImageResource(R.drawable.ic_start)
            binding.llBackUpState.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_green_4dp)
            binding.tvBackUpState.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.SolidGreen
                )
            )
        }
        UploadWorker.triggerImageUploadingWorker(requireContext().applicationContext)
    }



    private fun getSelectedEntity(idx: Int): (MutableList<BackupEntity>) {

        if (context != null) {
            val database =
                BackupRoomDatabase.getDatabase(requireContext()).backupDao()

//            historyEntity = database.getAllSavedData()

            val history: MutableList<BackupEntity> = mutableListOf<BackupEntity>()
            for (entity in historyEntity) {
                if (entity.uploadStatus == idx) {
                    history.add(entity)
                    continue
                }
            }
            return history
        } else {
            return ArrayList<BackupEntity>()
        }

    }

}

