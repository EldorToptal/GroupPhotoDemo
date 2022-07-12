package com.groupphoto.app.presentation.backuphistory

import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.data.repository.local.enums.BackUpFilesType
import com.groupphoto.app.databinding.FragmentBackUpFilesBinding
import com.groupphoto.app.presentation.backuphistory.adapter.BackupFilesAdapter
import com.groupphoto.app.presentation.base.BaseFragment
import com.groupphoto.app.util.Constants
import com.groupphoto.app.util.observe
import org.koin.androidx.viewmodel.ext.viewModel
import timber.log.Timber


class BackUpFilesFragment :
    BaseFragment<FragmentBackUpFilesBinding>(FragmentBackUpFilesBinding::inflate) {

    private val viewModel: BackUpHistoryViewModel by viewModel()
    private lateinit var adapter: BackupFilesAdapter

    companion object {
        const val TYPE = "TYPE"
        fun getInstance(backUpFileType: BackUpFilesType): BackUpFilesFragment {
            val args = Bundle()
            args.putSerializable(TYPE, backUpFileType)
            val backUpFilesInstance = BackUpFilesFragment()
            backUpFilesInstance.arguments = args
            return backUpFilesInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BackupFilesAdapter()
        binding.rvBackFiles.adapter = adapter
        observe(viewModel.backUpFiles, ::onFilesLoaded)
        val type = requireArguments().getSerializable(TYPE) as BackUpFilesType
        viewModel.getBackUpFilesByName(type).observe(viewLifecycleOwner, {
            Timber.tag(Constants.TAG_CHECK)
                .d("PagedList: ${it?.toString()}")
            adapter.submitList(it)
        })
    }

    private fun onFilesLoaded(files: ArrayList<BackupEntity>) {
//        adapter.submitList(files)
    }

}