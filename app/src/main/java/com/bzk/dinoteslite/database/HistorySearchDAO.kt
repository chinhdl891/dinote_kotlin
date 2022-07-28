package com.bzk.dinoteslite.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bzk.dinoteslite.model.HistorySearch

@Dao
interface HistorySearchDAO {
    @Insert
    fun onInsert(search: HistorySearch)

    @Query("delete from history_search")
    fun onDelete()

    @Query("select * from history_search")
    fun getListHistory(): LiveData<List<HistorySearch>>

}