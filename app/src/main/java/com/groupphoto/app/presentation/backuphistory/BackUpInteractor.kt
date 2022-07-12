package com.groupphoto.app.presentation.backuphistory

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.groupphoto.app.data.repository.local.dao.BackupRoomRepository
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.data.repository.local.enums.BackUpFilesType

interface BackUpInteractor {
    suspend fun getBackUpFiles(): List<BackupEntity>
    fun getBackUpFilesByName(
        type: BackUpFilesType,
        config: PagedList.Config
    ): LiveData<PagedList<BackupEntity>>
}

class BackUpInteractorImpl(val backupRoomRepository: BackupRoomRepository) : BackUpInteractor {

    override suspend fun getBackUpFiles(): List<BackupEntity> {
        return backupRoomRepository.getAllSavedData()
    }

    override fun getBackUpFilesByName(
        type: BackUpFilesType,
        config: PagedList.Config
    ): LiveData<PagedList<BackupEntity>> {
        return backupRoomRepository.getBackUpFilesByName(type,
            config)
    }
}