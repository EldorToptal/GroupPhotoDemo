package com.groupphoto.app.presentation.viewmodels

import android.content.Intent
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class SignUpWithEmailViewModel(
    private val groupPhotoRepository: GroupPhotoRepository

) : RxViewModel() {
    private val _event = SingleLiveEvent<SignUpWithEmailEvent>()
    val event: SingleLiveEvent<SignUpWithEmailEvent> get() = _event
    fun signupButtonClicked(email: String, intent : Intent) {
        groupPhotoRepository.firebaseSignUpWithEmail(email, onAuthenticated = { user ->
            _event.value = SignUpWithEmailEvent.OnSignUpButtonClicked(user)
        })
    }
}

sealed class SignUpWithEmailEvent {
    data class OnSignUpButtonClicked(val result: User?) : SignUpWithEmailEvent()
}
