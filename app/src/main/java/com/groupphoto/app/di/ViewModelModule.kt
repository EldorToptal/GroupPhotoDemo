package com.groupphoto.app.di


import com.groupphoto.app.presentation.auth.AuthViewModel
import com.groupphoto.app.presentation.backuphistory.BackUpHistoryViewModel
import com.groupphoto.app.presentation.main.profile.ProfileViewModel
import com.groupphoto.app.presentation.viewmodels.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SignupViewModel(get()) }
    viewModel { LogInWithEmailViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { SignUpWithEmailViewModel(get()) }
    viewModel { LogInWithEmailPasswordViewModel(get()) }
    viewModel { RegisterWithEmailPasswordViewModel(get()) }
    viewModel { SignInAppleModel(get()) }
    viewModel { LandingActivityViewModel(get()) }
    viewModel { PoolsListViewModel(get()) }
    viewModel { GalleryViewModel(get()) }
    viewModel { BackUpHistoryViewModel(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}