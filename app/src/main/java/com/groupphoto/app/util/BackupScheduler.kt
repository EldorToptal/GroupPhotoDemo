package com.groupphoto.app.util

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.groupphoto.app.data.repository.local.dao.BackupRoomDatabase
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.pawegio.kandroid.e


class BackupScheduler : JobService() {
    private val database =
        BackupRoomDatabase.getDatabase(
            application
        )
    private val backupDao = database.backupDao()

    private var jobCancelled = false
    override fun onStopJob(p0: JobParameters?): Boolean {
        e(TAG, "Job cancelled before completion");
        jobCancelled = true
        return true
    }
//    fun getAllSavedData(): List<BackupEntity> {
////        return backupDao.getAllSavedData()
//    }
    override fun onStartJob(params: JobParameters): Boolean {


        e(TAG, "Job started")
        Log.d(TAG, "onStartJob: 321")
        doBackgroundWork(params)
        return true;
    }

    private fun doBackgroundWork(params: JobParameters) {
        e("background work")

    }
    companion object {
        private const val TAG = "Scheduler "
    }
}