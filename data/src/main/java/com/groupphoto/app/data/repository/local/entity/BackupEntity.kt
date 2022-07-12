package com.groupphoto.app.data.repository.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "backups_table")
data class BackupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "fileName") val fileName: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "fileSize") val fileSize: Double,
    @ColumnInfo(name = "hash") val hash: String,
    @ColumnInfo(name = "isUploaded") var isUploaded: Boolean = false,
    @ColumnInfo(name = "fileType") var fileType: String,
    @ColumnInfo(name = "uploadStatus") var uploadStatus: Int = -1,
    @ColumnInfo(name = "uploadPercentage") var uploadPercentage: Int = 0,
    @ColumnInfo(name = "creationDate") var creationDate: String = ""
) : Serializable


