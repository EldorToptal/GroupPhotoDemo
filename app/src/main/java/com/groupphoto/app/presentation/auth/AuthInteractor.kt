package com.groupphoto.app.presentation.auth

import android.app.Activity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.groupphoto.app.data.repository.repository.AuthRepository
import com.groupphoto.app.data.repository.utils.network.LiveEvent
import com.groupphoto.app.data.repository.utils.network.Resource

interface AuthInteractor {
    fun signInWithGoogle(idToken: String, authResource: (Resource<AuthResult>) -> Unit)
    fun signInWithApple(
        activity: Activity,
        authResource: (Resource<AuthResult>) -> Unit
    )

    fun signUpWithEmail(
        email: String,
        password: String,
        authResource: (Resource<AuthResult>) -> Unit
    )

    fun signInWithEmail(
        email: String,
        password: String,
        authResource: (Resource<AuthResult>) -> Unit
    )
}

class AuthInteractorImpl(private val authRepository: AuthRepository) : AuthInteractor {

    override fun signInWithGoogle(idToken: String, authResource: (Resource<AuthResult>) -> Unit) {
        authRepository.signInWithGoogle(idToken, authResource)
    }

    override fun signInWithApple(
        activity: Activity,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        authRepository.signInWithApple(activity, authResource)
    }

    override fun signUpWithEmail(
        email: String,
        password: String,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        authRepository.signUpWithEmail(email, password, authResource)
    }

    override fun signInWithEmail(
        email: String,
        password: String,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        authRepository.signInWithEmail(email, password, authResource)
    }
}
