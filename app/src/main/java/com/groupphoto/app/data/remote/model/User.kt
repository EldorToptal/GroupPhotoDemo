package com.groupphoto.app.data.remote.model

import com.google.firebase.auth.FirebaseUser
import java.io.Serializable

public class User : Serializable {

    var uid: String? = null
    var name: String? = null
    var email: String? = null

    var isAuthenticated = false

    var isNew = false

    var isCreated = false

    var errorMsg: String? = null

    constructor() {}
    internal constructor(
        uid: String?,
        name: String?,
        email: String?,
        isAuthenticated: Boolean,
        errorMsg: String?
    ) {
        this.uid = uid
        this.name = name
        this.email = email
        this.isAuthenticated = isAuthenticated
        this.errorMsg = errorMsg
    }

    companion object{
        fun mapFirebaseUserToLocalUser(firebaseUser: FirebaseUser?): User {
            return User(
                firebaseUser?.uid,
                firebaseUser?.displayName,
                firebaseUser?.email,
                false,
                ""
            )
        }
    }
}