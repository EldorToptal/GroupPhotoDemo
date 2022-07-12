package com.groupphoto.app.util.extensions

import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun String.sha512(): String {
    val md = MessageDigest.getInstance("SHA-512")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun ImageView.loadImage(url: String?) =
    Glide.with(this.context).load(url).into(this)

fun ImageView.loadCircularImage(url: String?) =
    Glide.with(this.context).load(url)
        .apply(RequestOptions.circleCropTransform())
        .into(this)

fun String.convertUnixToUTC(): String {
    val date = Date(this.toLong()*1000L)
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
    format.timeZone = TimeZone.getTimeZone("GMT")
    return format.format(date)
}

fun Fragment.onBackPressedCustomAction(action: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
        override
        fun handleOnBackPressed() {
            action()
        }
    })
}