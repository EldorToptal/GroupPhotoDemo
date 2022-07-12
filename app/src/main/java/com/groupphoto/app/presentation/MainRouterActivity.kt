package com.groupphoto.app.presentation

import android.content.ContextWrapper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseOptions
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.presentation.auth.AuthActivity
import com.groupphoto.app.presentation.landing.LandingActivity
import com.pixplicity.easyprefs.library.Prefs

/**
 * Intro activity handles all the initial actions, and it does not have layout
 * - Shortcuts
 * - Notifications
 * - General Starting Navigation
 * - Any Activity related initializations should be handled here
 */
class MainRouterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO - Temporary Shared Preference
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        val options = FirebaseOptions.Builder()
            .setProjectId("group-photo-staging")
            .setApplicationId("1:570574929326:android:b2fa72a9cd123616631c3f")
            .setApiKey("AIzaSyCakDyT2EGlKjHyAQOvaWdvw6CKQDZOXZ0")
            // .setDatabaseUrl(...)
            // .setStorageBucket(...)
            .build()

        if (SharedPref.isUserLoggedIn) {
            LandingActivity.startActivity(this)
            finish()
        } else {
            AuthActivity.startActivity(this)
            finish()
        }
    }
}