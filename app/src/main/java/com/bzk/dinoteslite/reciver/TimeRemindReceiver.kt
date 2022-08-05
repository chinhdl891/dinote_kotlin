package com.bzk.dinoteslite.reciver

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
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
import com.bzk.dinoteslite.view.activity.MainActivity
import java.util.*
import kotlin.random.Random


class TimeRemindReceiver : BroadcastReceiver() {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, p1: Intent) {
        var timeRemindDefault: Long = getTimeRemind(context)
        var timeMemoryDefault: Long = getTimeMemory(context)

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

        val timeRemind = TimeRemind(69, timeRemindDefault, true)
        timeRemindList.add(0, timeRemind)
        val timeMemory = TimeRemind(70, timeMemoryDefault, true)
        timeRemindList.add(0, timeMemory)

        timeRemindList.filter {
            it.time < System.currentTimeMillis() && it.active
        }.forEach {
            it.time += AlarmManager.INTERVAL_DAY
            DinoteDataBase.getInstance(context)?.timeRemindDAO()?.onUpdateTime(it)
        }
        val listTimeSort = timeRemindList.filter {
            it.time > System.currentTimeMillis() && it.active
        }.sortedBy { timeModel -> timeModel.time }

        val timeRemindPendingIntent = listTimeSort[0]
        Log.d("aaa", "onReceive: ${timeRemindPendingIntent.time}")
        if (getHour(System.currentTimeMillis() + 10000) == getHour(timeMemoryDefault)) {
            val listIdDinote: List<Int>? =
                DinoteDataBase.getInstance(context)?.dinoteDAO()?.getAllId()
            val sizeList = listIdDinote?.size
            if (sizeList == 0) {
                gotoReceiver(context, timeRemindPendingIntent.time)
            } else {
                val random = sizeList?.let {
                    Random.nextInt(sizeList)
                }
                random?.let {
                    createNotificationDeepLink(context, it)
                }
            }
        } else {
            createNotification(context)
            gotoReceiver(context, timeRemindPendingIntent.time)
        }

    }

    private fun getHour(time: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    private fun gotoReceiver(context: Context, time: Long) {
        val reIntent = Intent(context, TimeRemindReceiver::class.java)
        val requestCode = AppConstant.REQUEST_GOTO_RECEIVER
        val pendingIntentRe: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context,
                requestCode,
                reIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getBroadcast(context,
                requestCode,
                reIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val type = AlarmManager.RTC_WAKEUP

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, time, pendingIntentRe)
        } else {
            alarmManager.set(type, time, pendingIntentRe)
        }
    }

    private fun createNotificationDeepLink(context: Context, id: Int) {
        Log.d("TAG", "createNotificationDeepLink: ")
        val channelId = context.getString(R.string.dinote_channel)
        val pendingIntent = NavDeepLinkBuilder(context).apply {
            setGraph(R.navigation.nav_graph)
            setDestination(R.id.detailFragment)
            setArguments(bundleOf().apply {
                putInt(DEEP_LINK_ID, id)
            })
        }.createPendingIntent()

        val builder = context.let {
            NotificationCompat.Builder(it, channelId)
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

        val notificationCoManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationCoManager?.createNotificationChannel(NotificationChannel(
            channelId,
            context.getString(R.string.txt_name),
            NotificationManagerCompat.IMPORTANCE_HIGH))
        notificationCoManager?.notify(68, builder.build())
    }

    private fun createNotification(context: Context) {
        val channelId = context.getString(R.string.dinote_channel)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val requestCode = AppConstant.REQUEST_CODE_ADD
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getActivity(context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification: Notification =
            Notification.Builder(context, channelId)
                .setContentText(context.getString(R.string.notifi_cation_content))
                .setContentTitle(context.getText(R.string.notifi_title))
                .setChannelId(channelId)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setContentIntent(pendingIntent)
                .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(
            NotificationChannel(channelId,
                context.getString(R.string.txt_name),
                NotificationManagerCompat.IMPORTANCE_HIGH))
        notificationManager?.notify(AppConstant.ID_NOTIFICATION, notification)
    }

    private fun getTimeRemind(context: Context): Long {
        return MySharedPreferences(context).getTimeRemindDefault()
    }

    private fun getTimeMemory(context: Context): Long {
        return MySharedPreferences(context).getTimeMemoryDefault()
    }
}