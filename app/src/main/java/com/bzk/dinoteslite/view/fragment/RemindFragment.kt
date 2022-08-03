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
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.FragmentRemidBinding
import com.bzk.dinoteslite.model.TimeRemind
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.RemindFragmentViewModel
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

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
        val timeDefault: Long? = activity?.let { MySharedPreferences(it).getTimeRemindDefault() }
        val calendar = Calendar.getInstance().apply {
            if (timeDefault != null) {
                timeInMillis = timeDefault
            }
        }
        setTimeToText(calendar.timeInMillis)
        mBinding.rcvRemindListTime.layoutManager = LinearLayoutManager(context)
        mBinding.rcvRemindListTime.adapter = timeRemindAdapter
        observer()
    }

    private fun observer() {
        remindFragmentViewModel.listTimeRemind.observe(this) {
            timeRemindAdapter?.init(it.sortedBy { timeRemind -> timeRemind.time })
        }
    }

    private fun setTimeToText(time: Long) {
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
            R.id.imv_remind_cancel -> activity?.onBackPressed()
            R.id.tv_time_select -> selectTimeDialog()
            R.id.btn_remind -> addRemind()
        }
    }

    private fun addRemind() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minus = calendar[Calendar.MINUTE]
        TimePickerDialog(activity,
            { timePicker, i1, i2 ->
                calendar[Calendar.HOUR_OF_DAY] = i1
                calendar[Calendar.MINUTE] = i2
                onAddTimeRemind(calendar.timeInMillis)
            }, hour, minus, false).show()
    }

    private fun onAddTimeRemind(timeInMillis: Long) {
        val timeNow: Long = getTimeNow()
        val time = if (timeInMillis < timeNow) {
            timeInMillis + AlarmManager.INTERVAL_DAY
        } else {
            timeInMillis
        }
        val timeRemind = TimeRemind(0, time, true)
        remindFragmentViewModel.insertTimeRemind(timeRemind)
        setAlarmRemind()
    }

    private fun getTimeNow(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.apply {
            set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun selectTimeDialog() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minus = calendar[Calendar.MINUTE]
        TimePickerDialog(activity,
            { timePicker, i, i2 ->
                calendar[Calendar.HOUR_OF_DAY] = i
                calendar[Calendar.MINUTE] = i2
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                val time = calendar.timeInMillis
                setTimeToText(time)
                getMainActivity()?.let { MySharedPreferences(it).pushTimeRemindDefault(time) }
                setAlarmRemind()
            },
            hour,
            minus,
            false).apply {
            setTitle("Choose Time")
            show()
        }
    }

    private fun setAlarmRemind() {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(activity, TimeRemindReceiver::class.java)
        val requestCode = AppConstant.REQUEST_CODE_REMIND
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(activity,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getBroadcast(activity,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val listTime: MutableList<TimeRemind> =
            (context?.let {
                DinoteDataBase.getInstance(it)?.timeRemindDAO()?.getListTimeRemind()
            } as MutableList<TimeRemind>?)!!

        val timeDefault: Long = context?.let { MySharedPreferences(it).getTimeRemindDefault() }!!
        val timeMemory: Long = context?.let { MySharedPreferences(it).getTimeMemoryDefault() }!!

        listTime.add(TimeRemind(68, timeDefault, true))
        listTime.add(TimeRemind(69, timeMemory, true))

        listTime.filter {
            it.time < System.currentTimeMillis() + 10000L && it.active
        }.forEach {
            it.time += AlarmManager.INTERVAL_DAY
            DinoteDataBase.getInstance(context!!)?.timeRemindDAO()?.onUpdateTime(it)
        }

        val timeRemind = listTime.filter { it.time > System.currentTimeMillis() }[0]
        Log.d(TAG, "setAlarmRemind: ${timeRemind.time}")
        val type = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, timeRemind.time, pendingIntent)
        } else {
            alarmManager.set(type, timeRemind.time, pendingIntent)
        }
    }

}