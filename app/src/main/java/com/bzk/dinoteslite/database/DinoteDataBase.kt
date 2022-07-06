package com.bzk.dinoteslite.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bzk.dinoteslite.model.*

@Database(entities = [Dinote::class, TagModel::class, TimeRemind::class, HistorySearch::class],
    version = 1,
    exportSchema = false)
@TypeConverters(TagConvert::class)
abstract class DinoteDataBase : RoomDatabase() {
    abstract fun dinoteDAO(): DinoteDAO
    abstract fun tagDAO(): TagDAO
    abstract fun timeRemindDAO(): TimeRemindDAO
    abstract fun searchDAO() : HistorySearchDAO;

    companion object {
        private val DB_NAME = "dinote.db"
        private var instance: DinoteDataBase? = null

        fun getInstance(context: Context): DinoteDataBase? {
            synchronized(this) {
                if (instance == null) {
                    instance = Room
                        .databaseBuilder(context.applicationContext,
                            DinoteDataBase::class.java,
                            DB_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
                return instance
            }
        }
    }
}