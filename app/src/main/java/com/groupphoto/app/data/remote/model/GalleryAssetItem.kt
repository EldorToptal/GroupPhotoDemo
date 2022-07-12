package com.groupphoto.app.data.remote.model

data class GalleryAssetItem(
    val authorUserId: String?,
    val authorUserSummary: AuthorUserSummary?,
    val createdAt: CreatedAtX?,
    val fileId: String?,
    val fileSummary: FileSummary?,
    val id: String?,
    val mediaType: String?,
    val numberOfRatings: Int?,
    val poolId: String?,
    val rating: Int?,
    val renderings: List<Rendering>?,
    val score: Int?,
    val takenAt: TakenAt?,
    val views: Int?
)