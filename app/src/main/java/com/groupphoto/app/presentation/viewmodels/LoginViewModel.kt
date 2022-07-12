package com.groupphoto.app.presentation.viewmodels

import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class LoginViewModel(
    private val groupPhotoRepository: GroupPhotoRepository
) : RxViewModel() {
    private val _event = SingleLiveEvent<LogInEvent>()
    val event: SingleLiveEvent<LogInEvent> get() = _event
    fun loginWithGmail(idToken: String?) {
        groupPhotoRepository.firebaseLogInWithGoogle(idToken, onAuthenticated = { user ->
            _event.value =
                LogInEvent.OnGoogleLogIn(user)
        } )
    }

}

sealed class LogInEvent {
    data class OnGoogleLogIn(val result: User?) : LogInEvent()
}
