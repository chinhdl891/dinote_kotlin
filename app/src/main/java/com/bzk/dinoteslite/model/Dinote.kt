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
    var title: String = "fake 1",
    @ColumnInfo
    var content: String = "content",
    @ColumnInfo
    var uriImage: String = "null",
    @ColumnInfo
    var desImage: String = "null",
    @ColumnInfo
    var dateCreate: Long = 0,
    @ColumnInfo
    var motion: Int = 1,
    @ColumnInfo
    var isLike: Int = 1,
    @ColumnInfo
    var ListTag: List<TagModel> = listOf(TagModel(0))
)