package com.bzk.dinoteslite.database.sharedPreferences

import android.content.Context


class MySharedPreferences(var context: Context) {

    companion object {
        private const val MY_DATA_LOCAL = "my_data"
        private const val TIME_REMIND_DEFAULT = "time_default"
    }

    fun pushTimeRemindDefault(timeRemind: Long) {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        mySharedPreferences.edit().putLong(TIME_REMIND_DEFAULT, timeRemind).apply()
    }

    fun getTimeRemindDefault(): Long {
        val mySharedPreferences = context.getSharedPreferences(MY_DATA_LOCAL, 0)
        return mySharedPreferences.getLong(TIME_REMIND_DEFAULT, 0L)
    }
}