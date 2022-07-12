package com.groupphoto.app.data.repository.local.enums

import java.io.Serializable

enum class BackUpFilesType(val type: String) : Serializable {
    ACTIVE("0"),
    COMPLETED("1"),
    FAILED("2"),
    NOT_DEFINED("-1")
}