package com.bzk.dinoteslite.view.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.FragmentRemidBinding
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.ReSizeView
import java.text.SimpleDateFormat
import java.util.*

class RemindFragment : BaseFragment<FragmentRemidBinding>(), View.OnClickListener {
    override fun getLayoutResource(): Int {
        return R.layout.fragment_remid
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        val timeDefault: Long = MySharedPreferences(mainActivity).getTimeRemindDefault()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeDefault
        }
        setTimeToText(calendar.timeInMillis)
    }

    fun setTimeToText(time: Long) {
        val simpleDateFormat = SimpleDateFormat(getString(R.string.hh_mm_a))
        val date = simpleDateFormat.format(Date(time))
        mBinding.tvTimeSelect.text = getString(R.string.time_remind, date)
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvRemindCancel, 64)
    }

    override fun onClick() {
        mBinding.imvRemindCancel.setOnClickListener(this)
        mBinding.tvTimeSelect.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_remind_cancel -> mainActivity.onBackPressed()
            R.id.tv_time_select -> selectTimeDialog()
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun selectTimeDialog() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minus = calendar[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(mainActivity,
            { timePicker, i, i2 ->
                calendar[Calendar.HOUR_OF_DAY] = i
                calendar[Calendar.MINUTE] = i2
                calendar[Calendar.SECOND] = 0
                val time = calendar.timeInMillis
                setTimeToText(time)
                MySharedPreferences(mainActivity).pushTimeRemindDefault(time)
                setAlarmRemind(time)
            },
            hour,
            minus,
            false).apply {
            setTitle("Choose Time")
            show()
        }
    }

    private fun setAlarmRemind(time: Long) {
        val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mainActivity, TimeRemindReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getBroadcast(mainActivity, 10, intent, PendingIntent.FLAG_IMMUTABLE)
        else PendingIntent.getBroadcast(mainActivity, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val type = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, time, pendingIntent)
        } else {
            alarmManager.set(type, time, pendingIntent)
        }
    }

}