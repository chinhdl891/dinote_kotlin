package com.bzk.dinoteslite.database

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
}