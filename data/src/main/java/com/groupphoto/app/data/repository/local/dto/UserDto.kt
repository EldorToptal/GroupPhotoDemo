package com.groupphoto.app.data.repository.local.dto

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserDto(
    val displayName: String? = "",
    val email: String? = "",
    val phoneNumber: String? = "",
    val providerId: String? = "",
    val uid: String? = "",
    val isEmailVerified: Boolean = false,
    val isAuthenticated: Boolean = false,
    val photoUrl: Uri? = null
) : Parcelable {

    fun mapFirebaseUserToUserDto(firebaseUser: FirebaseUser, isAuthenticated: Boolean) : UserDto{
        return UserDto(
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            phoneNumber = firebaseUser.phoneNumber,
            providerId = firebaseUser.providerId,
            uid= firebaseUser.uid,
            isEmailVerified = firebaseUser.isEmailVerified,
            photoUrl = firebaseUser.photoUrl,
            isAuthenticated = isAuthenticated
        )
    }
}