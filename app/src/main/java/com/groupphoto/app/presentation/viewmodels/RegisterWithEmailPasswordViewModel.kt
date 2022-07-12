package com.groupphoto.app.presentation.viewmodels

import android.content.Intent
import com.groupphoto.app.data.remote.model.User
import com.groupphoto.app.domain.GroupPhotoRepository
import com.groupphoto.app.coroutines.RxViewModel
import com.groupphoto.app.coroutines.SingleLiveEvent


class RegisterWithEmailPasswordViewModel(
    private val groupPhotoRepository: GroupPhotoRepository

) : RxViewModel() {
    private val _event = SingleLiveEvent<RegisterWithEmailPasswordEvent>()
    val event: SingleLiveEvent<RegisterWithEmailPasswordEvent> get() = _event
    fun registerButtonClicked(email: String,password: String, intent : Intent) {
        groupPhotoRepository.firebaseSignUpWithEmailPassword(email, password , onAuthenticated = { user ->
            _event.value =
                RegisterWithEmailPasswordEvent.OnRegisterButtonClicked(user)
        } )
    }
}

sealed class RegisterWithEmailPasswordEvent {
    data class OnRegisterButtonClicked(val result: User?) : RegisterWithEmailPasswordEvent()
}
