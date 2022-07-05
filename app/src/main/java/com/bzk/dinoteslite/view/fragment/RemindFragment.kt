package com.bzk.dinoteslite.view.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.TimeRemindAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.FragmentRemidBinding
import com.bzk.dinoteslite.model.TimeRemind
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.RemindFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

private const val TAG = "RemindFragment"

class RemindFragment : BaseFragment<FragmentRemidBinding>(), View.OnClickListener {
    private var timeRemindAdapter: TimeRemindAdapter? = null
    private val remindFragmentViewModel by lazy {
        ViewModelProvider(this)[RemindFragmentViewModel::class.java]
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_remid
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        timeRemindAdapter = TimeRemindAdapter(
            onSetCheck = {
                remindFragmentViewModel.update(it)
            },
            onDelete = {
                remindFragmentViewModel.deleteTimeRemind(it)
            })
        val timeDefault: Long = MySharedPreferences(mainActivity).getTimeRemindDefault()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeDefault
        }
        setTimeToText(calendar.timeInMillis)
        remindFragmentViewModel.listTimeRemind()
        mBinding.rcvRemindListTime.layoutManager = LinearLayoutManager(context)
        mBinding.rcvRemindListTime.adapter = timeRemindAdapter
        observer()
    }

    private fun observer() {
        remindFragmentViewModel.listTimeRemind.observe(this) {
            timeRemindAdapter?.init(it)
        }
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
        mBinding.btnRemind.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_remind_cancel -> mainActivity.onBackPressed()
            R.id.tv_time_select -> selectTimeDialog()
            R.id.btn_remind -> addRemind()
        }
    }

    private fun addRemind() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minus = calendar[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(mainActivity,
            { timePicker, i1, i2 ->
                calendar[Calendar.HOUR_OF_DAY] = i1
                calendar[Calendar.MINUTE] = i2
                calendar[Calendar.SECOND] = 0
                onAddTimeRemind(calendar.timeInMillis)
            }, hour, minus, false).show()
    }

    private fun onAddTimeRemind(timeInMillis: Long) {
        var time = 0L
        time = if (timeInMillis <= System.currentTimeMillis()) {
            timeInMillis + AlarmManager.INTERVAL_DAY
        } else {
            timeInMillis
        }
        val timeRemind = TimeRemind(id = 0, time = time, active = true)
        setAlarmRemind(time)
        remindFragmentViewModel.insertTimeRemind(timeRemind)
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
        val random = Random(50000)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(mainActivity,
                random.nextInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(mainActivity,
                random.nextInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val type = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, time, pendingIntent)
        } else {
            alarmManager.set(type, time, pendingIntent)
        }
    }

}