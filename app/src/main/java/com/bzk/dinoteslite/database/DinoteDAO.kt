package com.bzk.dinoteslite.database

import androidx.room.*
import com.bzk.dinoteslite.model.Dinote

@Dao
interface DinoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun onInsert(dinote: Dinote)

    @Delete
    fun onDelete(dinote: Dinote)

    @Update
    fun onUpdate(dinote: Dinote)

    @Query("select * from dinote where isLike = 1 order by dateCreate desc ")
    fun getAllDinoteFavorite(): List<Dinote>

    @Query("select * from dinote where title like '%' ||:search || '%' or content like '%' || :search || '%' or ListTag like '%' || :search || '%'")
    fun getListBySearch(search: String): List<Dinote>

    @Query("select COUNT(id) from dinote where title like '%' ||:search || '%' or content like '%' || :search || '%' or ListTag like '%' || :search || '%'")
    fun getTotalItemSearch(search: String): Int

    @Query("select * from dinote order by dateCreate desc limit :limit offset :next ")
    fun getAllDinote(limit: Int, next: Int): List<Dinote>

    @Query("select COUNT(id) from dinote")
    fun getTotalItemCount(): Int
}