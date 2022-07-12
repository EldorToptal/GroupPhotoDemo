package com.groupphoto.app.util

import com.groupphoto.app.data.remote.model.User

object Constants {
    const val INVALID_RESOURCE_ID = -1
    var g_user: User? = null
    const val KEY_LOGOUT = "logout"
    const val KEY_PWD = "password"
    const val KEY_LOGIN_TYPE = "loginType"
    const val KEY_EMAIL = "email"
    const val TAG_CHECK = "DATABASE_PAGINATION_TAG"
    const val UPLOAD_IMAGE_WORKER = "upload_image_worker"
    var totalSize = 2
}