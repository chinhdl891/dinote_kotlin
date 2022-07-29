package com.bzk.dinoteslite.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bzk.dinoteslite.model.Dinote
import kotlinx.coroutines.selects.select

@Dao
interface DinoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun onInsert(dinote: Dinote)

    @Delete
    fun onDelete(dinote: Dinote)

    @Update
    fun onUpdate(dinote: Dinote)

    @Query("select * from dinote where isLike = 1 order by dateCreate desc limit :limit offset :next")
    fun getAllDinoteFavorite(limit: Int, next: Int): LiveData<List<Dinote>>

    @Query("select * from dinote where title like '%' ||:search || '%' or content like '%' || :search || '%' or ListTag like '%' || :search || '%'")
    fun getListBySearch(search: String): List<Dinote>

    @Query("select COUNT(id) from dinote where title like '%' ||:search || '%' or content like '%' || :search || '%' or ListTag like '%' || :search || '%'")
    fun getTotalItemSearch(search: String): Int

    @Query("select * from dinote order by dateCreate desc limit :limit offset :next ")
    fun getAllDinote(limit: Int, next: Int): LiveData<List<Dinote>>

    @Query("select COUNT(id) from dinote")
    fun getTotalItemCount(): Int

    @Query("select id from dinote")
    fun getAllId(): List<Int>

    @Query("SELECT * FROM dinote WHERE id = :id LIMIT 1")
    fun getDinoteById(id: Int): Dinote

    @Query("select * from dinote order by dateCreate desc limit :limit ")
    fun getAllListLiveData(limit: Int): LiveData<List<Dinote>>

    @Query("select count(id) from dinote")
    fun getTotalItem(): Int

}