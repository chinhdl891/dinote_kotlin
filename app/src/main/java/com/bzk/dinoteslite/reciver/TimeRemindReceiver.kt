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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.model.TimeRemind
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.AppConstant.Companion.DEEP_LINK_ID
import com.bzk.dinoteslite.utils.AppConstant.Companion.REQUEST_CODE_NOTIFICATION
import com.bzk.dinoteslite.view.activity.MainActivity
import kotlin.random.Random

class TimeRemindReceiver : BroadcastReceiver() {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, p1: Intent) {
        var timeRemindDefault: Long = MySharedPreferences(context).getTimeRemindDefault()
        var timeMemoryDefault: Long = MySharedPreferences(context).getTimeMemoryDefault()

        if (timeRemindDefault < System.currentTimeMillis() + 10000) {
            timeRemindDefault += AlarmManager.INTERVAL_DAY
            MySharedPreferences(context).pushTimeRemindDefault(timeRemindDefault)
        }

        if (timeMemoryDefault < System.currentTimeMillis() + 10000) {
            timeMemoryDefault += AlarmManager.INTERVAL_DAY
            MySharedPreferences(context).pushTimeRemindDefault(timeMemoryDefault)
        }

        val timeRemindList: MutableList<TimeRemind> = mutableListOf()
        DinoteDataBase.getInstance(context)?.timeRemindDAO()?.getListTimeRemind()
            ?.let { timeRemindList.addAll(it) }

        val timeRemind = TimeRemind(0, timeRemindDefault, true)
        timeRemindList.add(timeRemind)
        val timeMemory = TimeRemind(1, timeMemoryDefault, true)
        timeRemindList.add(timeMemory)

        val listTimeSort = timeRemindList.filter {
            it.time > System.currentTimeMillis() && it.active
        }.sortedBy { timeModel -> timeModel.time }

        var timeRemindPendingIntent = listTimeSort[0]
        val timeRemindNext = listTimeSort[1]

        timeRemindList.filter {
            it.time < System.currentTimeMillis() && it.active
        }.forEach {
            it.time += AlarmManager.INTERVAL_DAY
            DinoteDataBase.getInstance(context)?.timeRemindDAO()?.onUpdateTime(it)
        }

        if (timeRemindPendingIntent.time == timeMemoryDefault) {
            val listIdDinote: List<Int>? =
                DinoteDataBase.getInstance(context)?.dinoteDAO()?.getAllId()
            val sizeList = listIdDinote?.size
            val random = sizeList?.let {
                Random.nextInt(it - 1)
            }
            random?.let {
                createNotificationDeepLink(context, it)
            }
        } else {
            createNotification(context)
        }

        val reIntent = Intent(context, TimeRemindReceiver::class.java)
        val requestCode = REQUEST_CODE_NOTIFICATION
        val pendingIntentRe: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context,
                requestCode,
                reIntent,
                PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context,
                requestCode,
                reIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val type = AlarmManager.RTC_WAKEUP

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type,
                timeRemindNext.time,
                pendingIntentRe)
        } else {
            alarmManager.set(type, timeRemindNext.time, pendingIntentRe)
        }
    }

    private fun createNotificationDeepLink(context: Context, id: Int) {
        val pendingIntent = NavDeepLinkBuilder(context).apply {
            setGraph(R.navigation.nav_graph)
            setDestination(R.id.detailFragment)
            setArguments(bundleOf().apply {
                putInt(DEEP_LINK_ID, id)
            })
        }.createPendingIntent()

        val builder = context.let {
            NotificationCompat.Builder(it, createNotificationChannel(context))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.my_notification))
                .setContentText(context.getString(R.string.content_notification))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.big_content_text))
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
        }
        val notificationCoManager = NotificationManagerCompat.from(context)
        notificationCoManager.notify(AppConstant.ID_NOTIFICATION, builder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val requestCode = AppConstant.REQUEST_CODE_NOTIFICATION
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
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
        notificationCoManager.notify(AppConstant.ID_NOTIFICATION, builder.build())
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