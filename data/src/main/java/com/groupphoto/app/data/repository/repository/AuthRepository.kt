package com.groupphoto.app.data.repository.repository

import android.app.Activity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.groupphoto.app.data.repository.exceptions.UserExistsException
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.data.repository.local.pref.UserPref
import com.groupphoto.app.data.repository.utils.network.ErrorResponse
import com.groupphoto.app.data.repository.utils.network.Resource
import com.groupphoto.app.data.repository.utils.toPrettyJson
import timber.log.Timber

interface AuthRepository {
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

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuth) : AuthRepository {

    override fun signInWithGoogle(idToken: String, authResource: (Resource<AuthResult>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                handleAuthResult(authTask, authResource)
            }
    }

    override fun signInWithApple(
        activity: Activity,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        val provider = OAuthProvider.newBuilder("apple.com")
        val pending = firebaseAuth.pendingAuthResult
        if (pending != null) {
            pending.addOnCompleteListener { authTask: Task<AuthResult> ->
                handleAuthResult(authTask, authResource)
            }
        } else {
            firebaseAuth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnCompleteListener { authTask: Task<AuthResult> ->
                    handleAuthResult(authTask, authResource)
                }
        }
    }

    override fun signUpWithEmail(
        email: String,
        password: String,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                handleAuthResult(authTask, authResource)
            }
    }

    override fun signInWithEmail(
        email: String,
        password: String,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                handleAuthResult(authTask, authResource)
            }

    }

    private fun handleAuthResult(
        authTask: Task<AuthResult>,
        authResource: (Resource<AuthResult>) -> Unit
    ) {
        if (authTask.isSuccessful) {
            if (firebaseAuth.currentUser != null) {
                Timber.d("Successful: ${firebaseAuth.currentUser?.toPrettyJson()}")
                Timber.d("Successful: ${authTask.result.user?.toPrettyJson()}")
                authTask.result.user?.let {
                    UserPref.saveFirebaseUser(
                        authTask.result,
                        true
                    )
                }
                authResource.invoke(Resource.Success(authTask.result))
            } else {
                Timber.d("Error: ${authTask.exception!!.message}")
                authResource.invoke(Resource.Error(ErrorResponse(exception = authTask.exception as UserExistsException)))
            }
        } else {
            Timber.d("IsNotSuccessful ${authTask.exception?.message}")
            authResource.invoke(Resource.Error(ErrorResponse(exception = authTask.exception!!)))
        }
    }
}