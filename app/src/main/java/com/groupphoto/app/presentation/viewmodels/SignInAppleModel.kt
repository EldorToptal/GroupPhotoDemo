package com.groupphoto.app.presentation.viewmodels

import android.app.Activity
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class SignInAppleModel(
        private val groupPhotoRepository: GroupPhotoRepository
    ) : RxViewModel() {

    private val _event = SingleLiveEvent<LogInAppleEvent>()
    val event: SingleLiveEvent<LogInAppleEvent> get() = _event

    fun loginApple(idToken: String?, ac : Activity) {
        groupPhotoRepository.firebaseSignInWithApple(idToken, ac, onAuthenticated = { user ->
            _event.value =
                LogInAppleEvent.OnAppleLogin(user)
        })
    }

}

sealed class LogInAppleEvent {
    data class OnAppleLogin(val result: User?) : LogInAppleEvent()
}