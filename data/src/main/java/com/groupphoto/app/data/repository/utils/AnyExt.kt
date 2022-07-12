package com.groupphoto.app.data.repository.utils

import com.google.gson.GsonBuilder

/**
 * Convert an Object to pretty json string
 */
fun Any.toPrettyJson(): String = GsonBuilder().setPrettyPrinting().create().toJson(this)