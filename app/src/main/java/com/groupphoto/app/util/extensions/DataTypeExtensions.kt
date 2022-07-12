package com.groupphoto.app.util.extensions


/**
 * Format a double's number of decimal places
 * @param digits number of decimal places
 */
fun Double.decimalPlaces(digits: Int): String = String.format("%.${digits}f", this)
