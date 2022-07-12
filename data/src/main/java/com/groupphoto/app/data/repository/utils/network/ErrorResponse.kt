package com.groupphoto.app.data.repository.utils.network

import com.groupphoto.app.data.repository.utils.ErrorCodes
import org.json.JSONObject
import java.lang.Exception

data class ErrorResponse(
    var message: String = "Error",
    val result: Boolean = false,
    var statusCode: Int = 0,
    var code: Int = 0,
    var exception: Exception = Exception(),
    var errorCodes: ErrorCodes = ErrorCodes.UNKNOWN,
    @Transient
    var jsonResponse: JSONObject = JSONObject()
)