package com.bzk.dinoteslite.reciver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.view.activity.MainActivity

class TimeRemindReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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
        var timeRemindDefault: Long = MySharedPreferences(context).getTimeRemindDefault()
        if (timeRemindDefault < System.currentTimeMillis() + 10000) {
            timeRemindDefault += AlarmManager.INTERVAL_DAY
            MySharedPreferences(context).pushTimeRemindDefault(timeRemindDefault)
        }
    }

    private fun createNotificationChannel(context: Context): String {
        val name = context.getString(R.string.dinote_channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val description = context.getString(R.string.des_notifi)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel("dinoteId", name, importance)
            notificationChannel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return name
    }

}