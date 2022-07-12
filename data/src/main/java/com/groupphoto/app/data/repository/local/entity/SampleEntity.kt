package com.groupphoto.app.data.repository.local.entity

import androidx.annotation.DrawableRes
import java.io.Serializable


data class SampleEntity(
    @DrawableRes val thumb: Int,
    val isCurrentAvatar: Boolean? = false
) : Serializable


