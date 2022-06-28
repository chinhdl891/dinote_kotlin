package com.bzk.dinoteslite.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "dinote")
data class Dinote(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var title: String = "No Title",
    @ColumnInfo
    var content: String = "No Content",
    @ColumnInfo
    var uriImage: String = "null",
    @ColumnInfo
    var desImage: String = "null",
    @ColumnInfo
    var dateCreate: Long = System.currentTimeMillis(),
    @ColumnInfo
    var motion: Int = 1,
    @ColumnInfo
    var isLike: Boolean = false,
    @ColumnInfo
    var ListTag: List<TagModel> = listOf(TagModel(0))
)