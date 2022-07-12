package com.groupphoto.app.data.repository.local.pref

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import com.groupphoto.app.data.repository.local.dto.UserDto


object SharedPref : KotprefModel() {
    var deviceId by stringPref()
    var userId by stringPref()
    var loginEmail by stringPref()
    var idToken by stringPref()
    var backupOption by stringPref()
    var backupFromHere by booleanPref()
    var backupPaused by booleanPref(false)
    var isUserLoggedIn by booleanPref(false)
    var loginType by intPref(LoginType.EMAIL.type)
    var password by stringPref("")
    var email by stringPref("")
    var backUpOptions by enumValuePref(BackupOptions.ENTIRE_LIBRARY)
    var isUploading by booleanPref(false)
}