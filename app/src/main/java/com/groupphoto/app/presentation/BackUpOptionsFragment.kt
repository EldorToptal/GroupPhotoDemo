package com.groupphoto.app.presentation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.groupphoto.app.components.GroupPhotoSelectionView
import com.groupphoto.app.data.repository.local.pref.BackupOptions
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.databinding.FragmentBackUpOptionsBinding
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.util.Constants
import com.groupphoto.app.worker.UploadWorker
import com.pawegio.kandroid.e
import java.util.concurrent.TimeUnit

class BackUpOptionsFragment :
    BaseFragment<FragmentBackUpOptionsBinding>(FragmentBackUpOptionsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hasToolbar = true
        binding.svBackUpOptions.selectionListener = object :
            GroupPhotoSelectionView.OnItemSelectedListener {
            override fun onItemSelected(position: Int) {
                when (position) {
                    0 -> {
                        SharedPref.backUpOptions = BackupOptions.ENTIRE_LIBRARY
                    }
                    1 -> {
                        SharedPref.backUpOptions = BackupOptions.FROM_NOW_ON
                    }
                    2 -> {
                        SharedPref.backUpOptions = BackupOptions.HUNDRED_PHOTOS
                    }
                    3 -> {
                        SharedPref.backUpOptions = BackupOptions.DO_NOT_BACK_UP
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            UploadWorker.triggerImageUploadingWorker(requireContext().applicationContext, true)
            SharedPref.backUpOptions = binding.svBackUpOptions.getSelectedOption()
            findNavController().popBackStack()
        }
    }

}