package com.groupphoto.app.data.repository.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.convertUnixToUTC(): String {
    val date = Date(this.toLong()*1000L)
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
    format.timeZone = TimeZone.getTimeZone("GMT")
    return format.format(date)
}