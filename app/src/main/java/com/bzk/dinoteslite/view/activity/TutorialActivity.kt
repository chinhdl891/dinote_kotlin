package com.bzk.dinoteslite.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.TutorialAdapter
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.ActivityTutorialBinding
import com.bzk.dinoteslite.model.SlideModel
import com.bzk.dinoteslite.utils.ReSizeView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TutorialActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial)
        setContentView(mBinding.root)
        val timeDefault: Long = MySharedPreferences(context = this).getTimeRemindDefault()
        if (timeDefault > 0L) {
            gotoMain()
        } else {
            setUpAdapter()
            setUpScroll()
            onClick()
        }
    }

    private fun gotoMain() {
        mBinding.lnlTutorial.visibility = View.GONE
        mBinding.imvTutorialSplash.visibility = View.VISIBLE
        ReSizeView.resizeView(mBinding.imvTutorialSplash, 300)
        lifecycleScope.launch {
            delay(2000L)
            val intent = Intent(this@TutorialActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setUpAdapter() {
        val tutorialAdapter = TutorialAdapter()
        tutorialAdapter.initData(getListSlide())
        mBinding.vpgTutorialSlide.adapter = tutorialAdapter
        mBinding.dotTutorialFragment.attachTo(mBinding.vpgTutorialSlide)
    }

    private fun onClick() {
        mBinding.tvTutorialContinue.setOnClickListener(this)
        mBinding.tvTutorialSkip.setOnClickListener(this)
        mBinding.btnTutorialGotoMain.setOnClickListener(this)
    }

    private fun setUpScroll() {
        mBinding.vpgTutorialSlide.registerOnPageChangeCallback(object : OnPageChangeCallback() {
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

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.tv_tutorial_skip, R.id.btn_tutorial_goto_main -> {
                val intent = Intent(this@TutorialActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
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