package com.groupphoto.app.data.remote.model

data class Metadata(
    val `client-local-identifier`: String?,
    val `creation-date`: String?,
    val duration: String?,
    val favorite: String?,
    val firebaseStorageDownloadTokens: String?,
    val `gp-backup-type`: String?,
    val hidden: String?,
    val `media-subtypes`: String?,
    val `media-type`: String?,
    val `mime-type`: String?,
    val `modification-date`: String?,
    val `original-file-name`: String?,
    val `pixel-height`: String?,
    val `pixel-width`: String?,
    val `sha-512-hash`: String?,
    val `source-type`: String?,
    val `user-id`: String?,
    val uti: String?
)