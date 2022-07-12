package com.groupphoto.app.data.repository.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {

    @Query("SELECT * from backups_table ORDER BY fileName ASC")
    fun getFlowData(): Flow<List<BackupEntity>>

    @Query("SELECT * from backups_table ORDER BY fileName ASC")
    fun getAllSavedData(): List<BackupEntity>

    @Query("SELECT * from backups_table ORDER BY fileName ASC")
    suspend fun getAllBackUpFilesData(): List<BackupEntity>

    @Query("SELECT * FROM backups_table WHERE uploadStatus = :type  ORDER BY fileName ASC ")
    fun getBackUpFilesByName(type: String): DataSource.Factory<Int, BackupEntity>

    @Query("SELECT * from backups_table WHERE isUploaded ORDER BY fileName ASC")
    fun getNewFiles(): List<BackupEntity>

    @Query("UPDATE backups_table SET isUploaded = 1 WHERE id = :id")
    fun setUploaded(id: Int)

    @Query("UPDATE backups_table SET uploadStatus = 0 WHERE id = :id")
    fun setUploading(id: Int)

    @Query("UPDATE backups_table SET uploadStatus = 1 WHERE id = :id")
    fun setUploadDone(id: Int)

    @Query("UPDATE backups_table SET uploadStatus = 2 WHERE id = :id")
    fun setUploadFailed(id: Int)

    @Query("UPDATE backups_table SET uploadPercentage = :percentage WHERE id = :id")
    fun setUploadPercentage(id: Int, percentage: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(backup: BackupEntity?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: List<BackupEntity>)

    @Query("DELETE FROM backups_table")
    fun deleteAll()

    @Query("SELECT * from backups_table WHERE id = :id")
    fun getByTrackId(id: Int): BackupEntity

    @Query("DELETE  from backups_table WHERE id = :id")
    fun deleteById(id: Int)
}