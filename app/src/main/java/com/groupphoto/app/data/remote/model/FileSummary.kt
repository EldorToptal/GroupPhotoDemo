package com.groupphoto.app.data.remote.model

data class FileSummary(
    val contentType: String?,
    val createdAt: CreatedAtXX?,
    val id: String?,
    val mediaAttributes: MediaAttributes?,
    val metadata: Metadata?,
    val originalName: String?,
    val sha512Hash: String?,
    val size: String?,
    val storageReference: String?,
    val uploaded: Boolean?,
    val uploadedAt: UploadedAt?,
    val uploaderUserId: String?
)