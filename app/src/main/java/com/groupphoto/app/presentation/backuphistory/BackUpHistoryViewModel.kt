package com.groupphoto.app.presentation.backuphistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import kotlinx.coroutines.launch
import com.groupphoto.app.data.repository.local.enums.BackUpFilesType
import com.groupphoto.app.util.Constants
import timber.log.Timber


class BackUpHistoryViewModel(private val backUpInteractor: BackUpInteractor) : ViewModel() {

    val backUpFiles = MutableLiveData<ArrayList<BackupEntity>>()
    var backUpFilesLiveData = MutableLiveData<PagedList<BackupEntity>>()
    fun getBackUpFiles() {
        viewModelScope.launch {
            val files = backUpInteractor.getBackUpFiles()
            backUpFiles.postValue(files as ArrayList<BackupEntity>)
        }
    }

    fun getBackUpFilesByName(type: BackUpFilesType) : LiveData<PagedList<BackupEntity>> {
        val config = PagedList.Config.Builder()
            .setPageSize(40)
            .setInitialLoadSizeHint(40)
            .setPrefetchDistance(40)
            .setEnablePlaceholders(false)
            .build()
        return backUpInteractor.getBackUpFilesByName(type, config)
    }
}