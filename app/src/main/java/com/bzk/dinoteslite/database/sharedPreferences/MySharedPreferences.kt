package com.bzk.dinoteslite.database.sharedPreferences

import android.content.Context


class MySharedPreferences(var context: Context) {

    companion object {
        private const val MY_DATA_LOCAL = "my_data"
        private const val TIME_REMIND_DEFAULT = "time_default"
        private const val TIME_MEMORY_DEFAULT = "time_memory"
        private const val THEME_DEFAULT = "theme_default"
    }

    fun pushTimeRemindDefault(timeRemind: Long) {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        mySharedPreferences.edit().putLong(TIME_REMIND_DEFAULT, timeRemind).apply()
    }

    fun getTimeRemindDefault(): Long {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        return mySharedPreferences.getLong(TIME_REMIND_DEFAULT, 0L)
    }

    fun pushTheme(theme: Int) {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        mySharedPreferences.edit().putInt(THEME_DEFAULT, theme).apply()
    }

    fun getTheme(): Int {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        return mySharedPreferences.getInt(THEME_DEFAULT, 0)
    }

    fun pushTimeMemoryDefault(timeRemind: Long) {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        mySharedPreferences.edit().putLong(TIME_MEMORY_DEFAULT, timeRemind).apply()
    }

    fun getTimeMemoryDefault(): Long {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        return mySharedPreferences.getLong(TIME_REMIND_DEFAULT, 0L)
    }
}