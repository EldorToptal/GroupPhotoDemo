package com.groupphoto.app

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkManager
import com.chibatching.kotpref.Kotpref
import com.google.firebase.FirebaseOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.di.dataDomainModule
import com.groupphoto.app.di.interactorsModule
import com.groupphoto.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import timber.log.Timber.*

class GroupPhoto : Application() {
    companion object {
        val uploadingImageFiles = MutableLiveData<ArrayList<BackupEntity>>(ArrayList())
        val uploadingImageFileCounter = MutableLiveData<Int>(0)
    }

    override fun onCreate() {
        super.onCreate()

        // Kotpref
        Kotpref.init(this)
        // Start Koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@GroupPhoto)
            modules(listOf(viewModelModule, dataDomainModule, interactorsModule))
        }

        val options = FirebaseOptions.Builder()
            .setProjectId("group-photo-staging")
            .setApplicationId("1:570574929326:android:b2fa72a9cd123616631c3f")
            .setApiKey("AIzaSyCakDyT2EGlKjHyAQOvaWdvw6CKQDZOXZ0")
            // .setDatabaseUrl(...)
            // .setStorageBucket(...)
            .build()

        Firebase.initialize(this, options, "staging")
        if (BuildConfig.DEBUG) {
        Timber.plant(DebugTree())
        }

    }

}
