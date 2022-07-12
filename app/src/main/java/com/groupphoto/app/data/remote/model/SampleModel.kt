package com.groupphoto.app.data.remote.model

import com.google.gson.annotations.SerializedName

data class SampleModel(
	val id: Int?,
	val name: String?,
	val year: Int?,
	val color: String?,
	@SerializedName("pantone_value") val pantoneValue: String?)