package com.bzk.dinoteslite.base

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences

class GlobalApp : Application() {

    override fun onCreate() {
        super.onCreate()
        onsetUpTheme()
    }

    private fun onsetUpTheme() {
        val theme: Int = MySharedPreferences(this).getTheme()
        val nightMode =
            if (theme == 1) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}