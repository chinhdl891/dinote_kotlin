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
    var title: String = "",
    @ColumnInfo
    var content: String = "",
    @ColumnInfo
    var uriImage: String = "",
    @ColumnInfo
    var desImage: String = "",
    @ColumnInfo
    var dateCreate: Long = System.currentTimeMillis(),
    @ColumnInfo
    var motion: Int = 1,
    @ColumnInfo
    var isLike: Boolean = false,
    @ColumnInfo
    var ListTag: List<TagModel> = listOf(TagModel(0))
)