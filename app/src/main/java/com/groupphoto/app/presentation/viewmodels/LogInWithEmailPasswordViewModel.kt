package com.groupphoto.app.presentation.viewmodels

import android.content.Intent
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class LogInWithEmailPasswordViewModel(
    private val groupPhotoRepository: GroupPhotoRepository

) : RxViewModel() {
    private val _event = SingleLiveEvent<LoginWithEmailPasswordEvent>()
    val event: SingleLiveEvent<LoginWithEmailPasswordEvent> get() = _event
    fun loginButtonClicked(email: String,password: String, intent : Intent) {
        groupPhotoRepository.firebaseLoginWithEmailPassword(email, password , onAuthenticated = { user ->
            _event.value =
                LoginWithEmailPasswordEvent.OnLoginButtonClicked(user)
        } )
    }
}

sealed class LoginWithEmailPasswordEvent {
    data class OnLoginButtonClicked(val result: User?) : LoginWithEmailPasswordEvent()
}
