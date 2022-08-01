package com.bzk.dinoteslite.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bzk.dinoteslite.model.TimeRemind

@Dao
interface TimeRemindDAO {
    @Insert
    fun onInsertTime(timeRemind: TimeRemind)

    @Delete
    fun onDeleteTime(timeRemind: TimeRemind)

    @Update
    fun onUpdateTime(timeRemind: TimeRemind)

    @Query("select * from remind")
    fun getListTimeRemind(): List<TimeRemind>

    @Query("select * from remind order by time desc")
    fun getListTimeRemindLiveData(): LiveData<List<TimeRemind>>
}