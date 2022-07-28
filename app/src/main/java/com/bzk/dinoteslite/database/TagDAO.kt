package com.bzk.dinoteslite.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bzk.dinoteslite.model.TagModel

@Dao
interface TagDAO {
    @Insert
    fun insert(tag: TagModel)

    @Delete
    fun delete(tag: TagModel)

    @Query("select * from tag")
    fun getListTag(): LiveData<List<TagModel>>

    @Query("select count(content) from tag where content = :tagContent")
    fun countTag(tagContent: String): Int

    @Query("select * from tag limit 10")
    fun getListHotTag(): LiveData<List<TagModel>>

    @Query("select * from tag where content = :contentTag")
    fun getListTag(contentTag: String): List<TagModel>
}