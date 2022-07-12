package com.groupphoto.app.data.repository.pojo

import com.groupphoto.app.data.repository.local.pref.UserPref
import java.io.Serializable
import java.util.*

data class UserInfoResponse(
    val avatarURL: String? = UserPref.photoUrl,
    val createdAt: Date? = Date(),
    val email: String? = UserPref.email,
    val fullName: String? = UserPref.displayName,
    val homePoolId: String? = "",
    val id: String? = "",
    val signedUpWithAuthUserId: String? = "",
    val username: String? = UserPref.userName,
    val active: Boolean? = false,
    val lastRenewedAt: Date? = Date(),
    val startsAt: Date? = Date(),
    val type: String? = "",
    val userId: String? = "",
    val usedBytes: Double? = 0.0,
    val planCapacityBytes: Long? = 0L,
    var isUserInfoRequest : Boolean = true
) : Serializable {
    fun getCapacityForSize(): String {
        return ((this.planCapacityBytes ?: 0) / 1000000000).toString() + "GB"
    }

    fun getProgressOfUsedBytes(): Int {
        return ((this.usedBytes ?: 1 / (this.planCapacityBytes ?: 1).toDouble()) * 100).toInt()
    }
}


data class FirebaseTimeStamp(
    val nanoseconds: Long? = 0L,
    val seconds: Long? = 0L,
)


enum class UserCollection(val collectionName: String) {
    USERS("users"),
    USERS_SERVICE_PLANS("user_service_plans")
}
