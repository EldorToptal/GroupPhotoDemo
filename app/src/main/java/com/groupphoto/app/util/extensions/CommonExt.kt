@file:JvmName("CommonExt")

package com.groupphoto.app.util.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment
import com.groupphoto.app.BuildConfig
import com.groupphoto.app.R
import timber.log.Timber

fun hasOreo() = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
fun hasLollipop() = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
fun hasNougat() = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
fun isKitKat() = (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.KITKAT)
fun hasKitKat() = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)

fun postLollipop(func: () -> Unit) {
    if (hasLollipop()) {
        func()
    }
}

fun screenslog(mess: String, tag: String = "Screens") {
    if (isLogsEnabled()) Timber.tag(tag).d(mess)
}

fun logd(mess: String, tag: String = "myLogs") {
    if (isLogsEnabled()) Timber.tag(tag).d(mess)
}

fun isLogsEnabled() = BuildConfig.DEBUG

fun preLollipop(func: () -> Unit) {
    if (hasLollipop().not()) {
        func()
    }
}

fun posKitKat(func: () -> Unit) {
    if (hasKitKat()) {
        func()
    }
}

fun Context.isTablet() = resources.getBoolean(R.bool.isTablet)
fun Fragment.isTablet() = resources.getBoolean(R.bool.isTablet)

/**
if (isTablet()) {
filtersTabs.tabGravity = TabLayout.GRAVITY_CENTER
filtersTabs.tabMode = TabLayout.MODE_FIXED
}
 */

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.getAppBuildVersion(): String? {
    val name = try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }

    return name
}
