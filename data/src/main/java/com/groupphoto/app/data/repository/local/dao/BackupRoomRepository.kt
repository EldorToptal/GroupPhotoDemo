package com.groupphoto.app.data.repository.local.dao

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.data.repository.local.enums.BackUpFilesType
import androidx.paging.LivePagedListBuilder
import timber.log.Timber


interface BackupRoomRepository {
    fun insert(result: BackupEntity?)
    fun insertAll(result: MutableList<BackupEntity>)
    suspend fun getAllSavedData(): List<BackupEntity>
    fun getById(id: Int): BackupEntity
    fun deleteById(id: Int)
    fun getBackUpFilesByName(
        type: BackUpFilesType,
        config: PagedList.Config
    ): LiveData<PagedList<BackupEntity>>
}

class BackupRoomRepositoryImpl(application: Application) : BackupRoomRepository {
    private val database =
        BackupRoomDatabase.getDatabase(
            application
        )
    private val backupDao = database.backupDao()

    override fun insert(result: BackupEntity?) {
        backupDao.insert(result)
    }

    override fun insertAll(backups: MutableList<BackupEntity>) {
        backupDao.insertAll(backups)
    }

    override suspend fun getAllSavedData(): List<BackupEntity> {
        return backupDao.getAllBackUpFilesData()
    }

    override fun getById(id: Int): BackupEntity {
        return backupDao.getByTrackId(id)
    }

    override fun deleteById(id: Int) {
        backupDao.deleteById(id)
    }

    override fun getBackUpFilesByName(
        type: BackUpFilesType,
        config: PagedList.Config
    ): LiveData<PagedList<BackupEntity>> {
        val backUpDaoFactory = backupDao.getBackUpFilesByName(type.type)
        Timber.tag("DATABASE_PAGINATION_TAG")
            .d("getBackUpFilesByName: ${backUpDaoFactory.toString()}")
        return LivePagedListBuilder<Int, BackupEntity>(backUpDaoFactory, config)
            .build()
    }
}