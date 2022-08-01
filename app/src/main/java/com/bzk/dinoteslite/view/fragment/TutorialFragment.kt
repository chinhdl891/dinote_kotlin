package com.bzk.dinoteslite.view.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.TutorialAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.FragmentTutorialBinding
import com.bzk.dinoteslite.model.SlideModel
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt


class TutorialFragment : BaseFragment<FragmentTutorialBinding>(), View.OnClickListener {
    private var timeRemindDefault: Long = 0L

    override fun getLayoutResource(): Int {
        return R.layout.fragment_tutorial
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        val timeDefault: Long? = context?.let { MySharedPreferences(it).getTimeRemindDefault() }
        if (timeDefault != null) {
            if (timeDefault > 0L) {
                gotoMain()
            } else {
                setUpAdapter()
                setUpScroll()
                setTimeMemory()
                setupTimeDefault()
            }
        }
    }

    private fun setupTimeDefault() {
        val timeDefault: Long? = context?.let { MySharedPreferences(it).getTimeRemindDefault() }
        if (timeDefault == 0L) {
            val calendar = timeDefault(9, 30)
            val time =
                if (calendar > System.currentTimeMillis())
                    calendar
                else {
                    calendar + AlarmManager.INTERVAL_DAY
                }
            timeRemindDefault = if (timeRemindDefault > time) time else timeRemindDefault
            activity?.let {
                MySharedPreferences(it).pushTimeRemindDefault(time)
            }
            setNotificationDefault(timeRemindDefault)
        }
    }

    private fun setNotificationDefault(time: Long) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimeRemindReceiver::class.java)
        val requestCode = AppConstant.REQUEST_CODE_NOTIFICATION_DEFAULT
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.getBroadcast(context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE.or(PendingIntent.FLAG_UPDATE_CURRENT)) else PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val type = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(type, time, pendingIntent)
        } else {
            alarmManager.set(type, time, pendingIntent)
        }
    }

    private fun setTimeMemory() {
        val timeMemory: Long? = activity?.let { MySharedPreferences(it).getTimeMemoryDefault() }
        if (timeMemory == 0L) {
            val calendar = timeDefault(7)
            val time =
                if (calendar > System.currentTimeMillis())
                    calendar
                else {
                    calendar + AlarmManager.INTERVAL_DAY
                }
            timeRemindDefault = time
            activity?.let {
                MySharedPreferences(it).pushTimeMemoryDefault(time)
            }
        }
    }

    private fun timeDefault(hour: Int, minus: Int = 0): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minus)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun setUpScroll() {
        mBinding.vpgTutorialSlide.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    in 1..2 -> {
                        mBinding.tvTutorialSkip.visibility = View.VISIBLE
                        mBinding.lnlTutorialGotoMain.visibility = View.GONE
                        mBinding.clTutorialSlide.visibility = View.VISIBLE
                    }
                    0 -> {
                        mBinding.tvTutorialSkip.visibility = View.INVISIBLE
                        mBinding.lnlTutorialGotoMain.visibility = View.GONE
                        mBinding.clTutorialSlide.visibility = View.VISIBLE
                    }
                    else -> {
                        mBinding.lnlTutorialGotoMain.visibility = View.VISIBLE
                        mBinding.clTutorialSlide.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setUpAdapter() {
        val tutorialAdapter = TutorialAdapter()
        tutorialAdapter.initData(getListSlide())
        mBinding.vpgTutorialSlide.adapter = tutorialAdapter
        mBinding.dotTutorialFragment.attachTo(mBinding.vpgTutorialSlide)
    }

    private fun getListSlide(): List<SlideModel> {
        return listOf(
            SlideModel(R.drawable.imv_tutorial_st,
                getString(R.string.txt_dinote_title),
                getString(R.string.txt_dinote)),
            SlideModel(R.drawable.imv_tutorial_nd,
                getString(R.string.txt_backup_and_security_title),
                getString(R.string.txt_backup_and_security)),
            SlideModel(R.drawable.imv_tutorial_rd,
                getString(R.string.txt_theme_title),
                getString(R.string.txt_theme)),
            SlideModel(R.drawable.imv_tutorial_4th,
                getString(R.string.txt_go_to_main),
                getString(R.string.txt_go_to_main_des))
        )
    }

    private fun gotoMain() {
        mBinding.lnlTutorial.visibility = View.GONE
        mBinding.imvTutorialSplash.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(2000L)
        }
        findNavController().navigate(R.id.mainFragment)
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvTutorialSplash, 300)
    }

    override fun onClick() {
        mBinding.tvTutorialContinue.setOnClickListener(this)
        mBinding.tvTutorialSkip.setOnClickListener(this)
        mBinding.btnTutorialGotoMain.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_tutorial_skip, R.id.btn_tutorial_goto_main -> {
                findNavController().navigate(R.id.mainFragment)
            }
            R.id.tv_tutorial_continue -> {
                var itemPage = mBinding.vpgTutorialSlide.currentItem
                if (itemPage in 0..2) {
                    itemPage++
                    mBinding.vpgTutorialSlide.currentItem = itemPage
                }
            }
        }
    }

}