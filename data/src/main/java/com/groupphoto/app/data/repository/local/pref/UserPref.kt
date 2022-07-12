package com.groupphoto.app.data.repository.local.pref

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.bulk
import com.google.firebase.auth.AuthResult

object UserPref : KotprefModel() {
    var displayName: String by stringPref("")
    var email: String by stringPref("")
    var phoneNumber: String by stringPref("")
    var providerId: String by stringPref("")
    var uid: String by stringPref("")
    var isEmailVerified: Boolean by booleanPref(false)
    var isAuthenticated: Boolean by booleanPref(false)
    var photoUrl: String by stringPref("")
    var isNewUser: Boolean by booleanPref(false)
    var userName: String by stringPref("")
    fun saveFirebaseUser(
        authResult: AuthResult,
        isAuthenticated: Boolean = false
    ) {
        UserPref.bulk {
            this.displayName = authResult.user?.displayName.toString()
            this.email = authResult.user?.email.toString()
            this.phoneNumber = authResult.user?.phoneNumber.toString()
            this.providerId = authResult.user?.providerId.toString()
            this.uid = authResult.user?.uid.toString()
            this.isAuthenticated = isAuthenticated
            this.isEmailVerified = authResult.user?.isEmailVerified == true
            this.photoUrl = authResult.user?.photoUrl.toString()
            this.isNewUser = authResult.additionalUserInfo?.isNewUser == true
            this.userName = authResult.additionalUserInfo?.username.toString()
        }
    }
}