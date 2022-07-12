package com.groupphoto.app.util.extensions

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.util.Patterns
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Sanitize
 */
fun String.sanitize(): String =
    this.replace("[^\\s]([ ]{2,})[^\\s]".toRegex(), " ")
        .replace("\n\n\n+".toRegex(), "\n\n")
        .trim()

/**
 * Phone number sanitation
 */
fun String.sanitizePhoneNumber(): String =
    this.sanitize()
        .replace("-", "")
        .replace("(", "")
        .replace(")", "")
        .replace(" ", "")
        .trim()


/**
 * Prefix a url with "http" if it isn't yet prefixed
 */
fun String.precedeLinkWithHttp() = if (!this.startsWith("http")) "http://$this" else this

/**
 * Prefix a url with "https" if it isn't yet prefixed
 */
fun String.precedeLinkWithHttps() = if (!this.startsWith("http")) "https://$this" else this

/**
 * Checks if starts with a vowel
 */
fun String.startsWithVowel(): Boolean = "AEIOUaeiou".indexOf(first()) != -1

/**
 * Check if valid name
 */
fun String.isValidName(): Boolean =
    Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z])[a-zA-Z',.-]*)*\$").matcher(this).matches()

/**
 * Check if valid email
 */
fun String.isValidEmail(): Boolean = matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$".toRegex())

/**
 * Check if strong password
 */
fun String.isStrongPassword(): Boolean = matches("(?=^.{6,}\$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*\$".toRegex())

/**
 * Check if valid phone number
 */
fun String.isValidPhoneNumber(): Boolean = android.util.Patterns.PHONE.matcher(this).matches()

/**
 * Check if valid PH phone number
 */
fun String.isValidPHPhoneNumber(): Boolean =
    Pattern.compile("^(09|\\+639)\\d{9}$", Pattern.CASE_INSENSITIVE).matcher(this).find()

/**
 * Check if valid IP address
 */
fun String.isValidIpAddress(): Boolean {
    return if (this.sanitize().isNotEmpty()) Patterns.IP_ADDRESS.matcher(this.sanitize()).matches() else false
}

/**
 * URL Encode for URL queries etc.
 */
fun String.urlEncode(): String = URLEncoder.encode(this, "utf-8")

/**
 * Convert to Editable for Edit text utility
 */
fun String.toEditable() = SpannableStringBuilder(this)

/**
 * Convert date string to Date Object
 * @param fromFormat date string format to convert from
 */
@SuppressLint("SimpleDateFormat")
fun String.dateStringToDate(fromFormat: String = "yyyy-MM-dd"): Date {
    val dateFormat = SimpleDateFormat(fromFormat)
    return dateFormat.parse(this)
}

/**
 * Convert date string to another date string format
 * @param fromFormat date string format to convert from
 * @param toFormat date string format to convert to
 */
@SuppressLint("SimpleDateFormat")
fun String.dateStringToFormattedDate(
    fromFormat: String = "yyyy-MM-dd",
    toFormat: String = "MMMM dd, yyyy"): String {
    return try {
        val dateFormat = SimpleDateFormat(fromFormat)
        val date = dateFormat.parse(this)

        SimpleDateFormat(toFormat).format(date)
    } catch (e: Exception) {
        this
    }
}

/**
 * Convert date string to millis
 * @param fromFormat date string format to convert from
 */
@SuppressLint("SimpleDateFormat")
fun String.dateToMillis(fromFormat: String = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"): Long {
    return try {
        SimpleDateFormat(fromFormat).parse(this).time
    } catch (e: Exception) {
        0.toLong()
    }
}