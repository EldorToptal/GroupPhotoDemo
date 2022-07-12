package com.groupphoto.app.presentation.viewmodels

import android.content.Intent
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class LogInWithEmailViewModel(
    private val groupPhotoRepository: GroupPhotoRepository

) : RxViewModel() {
    private val _event = SingleLiveEvent<LoginWithEmailEvent>()
    val event: SingleLiveEvent<LoginWithEmailEvent> get() = _event
    fun loginButtonClicked(email: String, intent : Intent) {
        groupPhotoRepository.firebaseSignInWithEmail(email,intent, onAuthenticated = { user ->
            _event.value =
                LoginWithEmailEvent.OnLoginButtonClicked(user)
        } )
    }
}

sealed class LoginWithEmailEvent {
    data class OnLoginButtonClicked(val result: User?) : LoginWithEmailEvent()
}
