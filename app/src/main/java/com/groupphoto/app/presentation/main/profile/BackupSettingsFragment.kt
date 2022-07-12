package com.groupphoto.app.presentation.main.profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.groupphoto.app.GroupPhoto
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.databinding.FragmentBackupSettingsBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.worker.UploadWorker


class BackupSettingsFragment :
    BaseFragment<FragmentBackupSettingsBinding>(FragmentBackupSettingsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tbBackup.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.svBackUpOptions.selectBackUpOption(SharedPref.backUpOptions)
        binding.btnSave.setOnClickListener {
            SharedPref.backUpOptions = binding.svBackUpOptions.getSelectedOption()
            showMessageSnackbar("Successfully saved")
            UploadWorker.triggerImageUploadingWorker(requireContext().applicationContext, true)
        }

        GroupPhoto.uploadingImageFileCounter.observe(viewLifecycleOwner, Observer {
            val fileSize = GroupPhoto.uploadingImageFiles.value?.size ?: 1
            if (fileSize != 0) {
                showUiOfProgress(it + 1, fileSize)
            }
        })
//        GroupPhoto.uploadingImageFiles.observe(viewLifecycleOwner, Observer {
////            GroupPhoto.uploadingImageFileCounter.value = GroupPhoto.uploadingImageFileCounter.value
//        })


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

}