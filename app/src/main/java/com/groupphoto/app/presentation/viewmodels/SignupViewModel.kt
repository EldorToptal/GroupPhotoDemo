package com.groupphoto.app.presentation.viewmodels

import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class SignupViewModel(
    private val groupPhotoRepository: GroupPhotoRepository
) : RxViewModel() {
    private val _event = SingleLiveEvent<SignupEvent>()
    val event: SingleLiveEvent<SignupEvent> get() = _event
    fun signUpGmailClicked(idToken: String?) {
        groupPhotoRepository.firebaseSignInWithGoogle(idToken, onAuthenticated = { user ->
            _event.value =
                SignupEvent.OnGoogleSignUp(user)
        } )
    }
}

sealed class SignupEvent {
    data class OnGoogleSignUp(val result: User?) : SignupEvent()
}
