package com.groupphoto.app.data.remote.model

data class PoolItem(
    val colorTag: String?,
    val createdAt: CreatedAt?,
    val createdByUserId: String?,
    val defaultRole: String?,
    val description: String?,
    val id: String?,
    val isArchived: Boolean?,
    var title: String?,
    val type: String?,
    val updatedAt: UpdatedAt?,
    val userId: String?
)