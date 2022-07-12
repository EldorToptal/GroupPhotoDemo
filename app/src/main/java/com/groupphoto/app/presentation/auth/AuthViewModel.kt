package com.groupphoto.app.presentation.auth

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.data.repository.utils.network.LiveEvent
import com.groupphoto.app.data.repository.utils.network.Resource
import com.groupphoto.app.presentation.base.BaseViewModel

class AuthViewModel(val interactor: AuthInteractor) : BaseViewModel() {

    val authResource: LiveEvent<AuthResult> = LiveEvent()
    var authOption: MutableLiveData<AuthOption> = MutableLiveData()

    init {
        authOption.value = (AuthOption.LOGIN)
    }

    fun signInWithGoogle(idToken: String) {
        loading.value = true
        try {
            interactor.signInWithGoogle(idToken, ::onAuthResourceLoaded)
        } catch (e: Exception) {
            errorProcess(e)
        }
    }

    fun signInWithApple(activity: Activity) {
        loading.value = true
        try {
            interactor.signInWithApple(activity, ::onAuthResourceLoaded)
        } catch (e: Exception) {
            errorProcess(e)
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        loading.value = true
        try {
            interactor.signUpWithEmail(email, password, ::onAuthResourceLoaded)
        } catch (e: Exception) {
            errorProcess(e)
        }
    }


    fun signInWithEmail(email: String, password: String) {
        loading.value = true
        try {
            interactor.signInWithEmail(email, password, ::onAuthResourceLoaded)
        } catch (e: Exception) {
            errorProcess(e)
        }
    }

    private fun onAuthResourceLoaded(resource: Resource<AuthResult>) {
        when (resource) {
            is Resource.Success -> {
                SharedPref.isUserLoggedIn = true
                authResource.postValue(resource.data)
            }
            is Resource.Error -> {
                errorProcess(resource.errorResponse.exception)
            }
        }
    }

    fun toggleAuthOption() {
        authOption.value =
            if (authOption.value == AuthOption.LOGIN)
                AuthOption.SIGNUP
            else
                AuthOption.LOGIN
    }

    enum class AuthOption {
        LOGIN, SIGNUP
    }

}