package com.bzk.dinoteslite.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagModel(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    @ColumnInfo(name = "content")
    var contentTag: String = ""
)