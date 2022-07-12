package com.groupphoto.app.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.groupphoto.app.data.remote.GroupPhotoService
import com.groupphoto.app.data.repository.local.dao.BackupRoomRepository
import com.groupphoto.app.data.repository.local.dao.BackupRoomRepositoryImpl
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.domain.GroupPhotoRepositoryImpl
import com.groupphoto.app.coroutines.AppSchedulerProvider
import com.groupphoto.app.coroutines.SchedulerProvider
import com.groupphoto.app.data.repository.repository.AuthRepository
import com.groupphoto.app.data.repository.repository.AuthRepositoryImpl
import com.groupphoto.app.data.repository.repository.UserRepository
import com.groupphoto.app.data.repository.repository.UserRepositoryImpl
import org.koin.dsl.module

val dataDomainModule = module {
    single<GroupPhotoRepository>() {
        GroupPhotoRepositoryImpl(get())
    }

    single<BackupRoomRepository>(createdAtStart = true) { BackupRoomRepositoryImpl(get()) }

    single<SchedulerProvider>(createdAtStart = true) { AppSchedulerProvider() }

    single { GroupPhotoService() }

    single { FirebaseAuth.getInstance() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<UserRepository> { UserRepositoryImpl(get()) }

    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }


}

