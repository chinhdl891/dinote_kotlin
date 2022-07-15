package com.bzk.dinoteslite.reciver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.model.TimeRemind
import com.bzk.dinoteslite.view.activity.MainActivity
import kotlin.random.Random

private const val TAG = "TimeRemindReceiver"

class TimeRemindReceiver : BroadcastReceiver() {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, p1: Intent) {
        createNotification(context)
        var timeRemindDefault: Long = MySharedPreferences(context).getTimeRemindDefault()
        if (timeRemindDefault < System.currentTimeMillis() + 10000) {
            timeRemindDefault += AlarmManager.INTERVAL_DAY
            MySharedPreferences(context).pushTimeRemindDefault(timeRemindDefault)
        }

        val timeRemindList: MutableList<TimeRemind> = mutableListOf()
        DinoteDataBase.getInstance(context)?.timeRemindDAO()?.getListTimeRemind()
            ?.let { timeRemindList.addAll(it) }

        val timeRemind = TimeRemind(0, timeRemindDefault, true)
        timeRemindList.add(timeRemind)

        val timeRemindPendingIntent = timeRemindList.filter {
            it.time > System.currentTimeMillis() && it.active
        }.sortedBy { time -> time.time }[0]


        timeRemindList.filter {
            it.time < System.currentTimeMillis() && it.active
        }.forEach {
            it.time += AlarmManager.INTERVAL_DAY
            DinoteDataBase.getInstance(context)?.timeRemindDAO()?.onUpdateTime(it)
        }

        val reIntent = Intent(context, TimeRemindReceiver::class.java)
        val random = Random(50000)
        val pendingIntentRe: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context,
                random.nextInt(),
                reIntent,
                PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context,
                random.nextInt(),
                reIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val type = AlarmManager.RTC_WAKEUP

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type,
                timeRemindPendingIntent.time,
                pendingIntentRe)
        } else {
            alarmManager.set(type, timeRemindPendingIntent.time, pendingIntentRe)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, Random(50000).nextInt(), intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, Random(50000).nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(context, createNotificationChannel(context))
        builder.apply {
            setContentTitle(context.getText(R.string.notifi_title))
            setContentText(context.getText(R.string.notifi_cation_content))
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            setSmallIcon(R.drawable.ic_text_loved)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_HIGH
        }
        val notificationCoManager = NotificationManagerCompat.from(context)
        notificationCoManager.notify(123, builder.build())
    }

    private fun createNotificationChannel(context: Context): String {
        val name = context.getString(R.string.dinote_channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = context.getString(R.string.des_notifi)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("dinoteId", name, importance)
            notificationChannel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return name
    }

}