package com.groupphoto.app.di

import com.groupphoto.app.presentation.auth.AuthInteractor
import com.groupphoto.app.presentation.auth.AuthInteractorImpl
import com.groupphoto.app.presentation.backuphistory.BackUpInteractor
import com.groupphoto.app.presentation.backuphistory.BackUpInteractorImpl
import com.groupphoto.app.presentation.main.profile.ProfileInteractor
import com.groupphoto.app.presentation.main.profile.ProfileInteractorImpl
import org.koin.dsl.module

val interactorsModule = module {

    factory<AuthInteractor> { AuthInteractorImpl(get()) }

    factory<ProfileInteractor> { ProfileInteractorImpl(get()) }

    factory<BackUpInteractor> { BackUpInteractorImpl(get()) }
}