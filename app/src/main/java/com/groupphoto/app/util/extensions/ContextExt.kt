package com.groupphoto.app.util.extensions

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.groupphoto.app.R
import com.pawegio.kandroid.connectivityManager
import com.pawegio.kandroid.e
import java.io.File

//region Toast
fun Context.toastSH(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.toastLN(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Fragment.toastSH(text: CharSequence) {
    requireContext().toastLN(text)
}

fun Fragment.toastSH(@StringRes idResource: Int) {
    requireContext().toastLN(getString(idResource))
}

fun Fragment.toastLN(@StringRes idResource: Int) {
    requireContext().toastLN(getString(idResource))
}

fun Fragment.toastLN(text: CharSequence) {
    requireContext().toastLN(text)
}
//endregion Toast

/**
 * Check if internet is available
 * returns [Boolean]
 */
fun Context.isInternetAvailable() = connectivityManager?.activeNetworkInfo?.isConnected == true

/**
 * Handle connection availability
 * @param available callback for when internet is available
 * @param unavailable callback for when internet is not available
 */
fun Context.internetConnectivityHandler(available: (() -> Unit), unavailable: (() -> Unit)? = null) {
    if (isInternetAvailable()) available.invoke() else unavailable?.invoke()
}

/**
 * Get default status bar height
 */
fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * Get theme accent color
 */
fun Context.getAccentColor(): Int {
    val a = obtainStyledAttributes(TypedValue().data, intArrayOf(R.attr.colorAccent))
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

/**
 * Get color resource
 * @param color color resource
 */
fun Context.getCompatColor(@ColorRes color: Int) = ContextCompat.getColor(this, color)

/**
 * Get drawable resource
 * @param drawable resource
 */
fun Context.getCompatDrawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

/** Clear app data **/
fun Context.clearAppData() {
    val appDir = File(cacheDir.parent)
    if (appDir.exists()) {
        for (dir in appDir.list()) {
            if (dir != "lib") {
                deleteDirectory(File(appDir, dir))
                e("File /data/data/APP_PACKAGE/$dir DELETED")
            }
        }
    }
}

/**
 * Delete file directory
 */
fun Context.deleteDirectory(dir: File): Boolean {
    if (dir.isDirectory) {
        for (i in dir.list().indices) {
            try {
                deleteDirectory(File(dir, dir.list()[i]))
            } catch (e: ArrayIndexOutOfBoundsException) {
                return false
            }
        }
    }
    return dir.delete()
}


