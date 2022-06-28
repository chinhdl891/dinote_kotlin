package com.bzk.dinoteslite.database

import androidx.room.*
import com.bzk.dinoteslite.model.Dinote

@Dao
interface DinoteDAO {
    @Insert
    fun onInsert(dinote: Dinote)

    @Delete
    fun onDelete(dinote: Dinote)

    @Update
    fun onUpdate(dinote: Dinote)

    @Query("select * from dinote where isLike = 1 order by dateCreate desc ")
    fun getAllDinoteFavorite(): List<Dinote>

    @Query("select * from dinote")
    fun getAllDinote(): List<Dinote>
}