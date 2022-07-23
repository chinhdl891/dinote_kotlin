package com.bzk.dinoteslite.view.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.ActivityMainBinding
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.AppConstant
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        onsetUpTheme()
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(mBinding.root)
        checkPermissions()
        setTimeMemory()
        setupTimeDefault()
    }

    private fun setTimeMemory() {
        val timeMemory: Long = MySharedPreferences(this).getTimeMemoryDefault()
        if (timeMemory == 0L) {
            val calendar = timeDefault(7)
            val time =
                if (calendar > System.currentTimeMillis())
                    calendar
                else
                    calendar + AlarmManager.INTERVAL_DAY
            MySharedPreferences(this).pushTimeMemoryDefault(time)
            setNotificationDefault(time)
        }
    }

    private fun timeDefault(hour: Int, minus : Int = 0): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minus)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun setupTimeDefault() {
        val timeDefault: Long = MySharedPreferences(this).getTimeRemindDefault()
        if (timeDefault == 0L) {
            val calendar = timeDefault(9, 30)
            val time =
                if (calendar > System.currentTimeMillis())
                    calendar
                else
                    calendar + AlarmManager.INTERVAL_DAY
            MySharedPreferences(this).pushTimeRemindDefault(time)
        }
    }

    private fun setNotificationDefault(time: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TimeRemindReceiver::class.java)
        val random = Random(1000).nextInt()
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(this,
                random,
                intent,
                PendingIntent.FLAG_IMMUTABLE) else PendingIntent.getBroadcast(this,
                random,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val type = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, time, pendingIntent)
        } else {
            alarmManager.set(type, time, pendingIntent)
        }
    }

    private fun onsetUpTheme() {
        val theme: Int = MySharedPreferences(this).getTheme()
        val nightMode =
            if (theme == 1) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstant.PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.write_debied, Toast.LENGTH_SHORT).show()
            }
            checkPermissions()
        }

    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE),
                AppConstant.PERMISSION_WRITE_EXTERNAL_STORAGE)
        }
    }
}
