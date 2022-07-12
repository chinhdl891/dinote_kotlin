package com.bzk.dinoteslite.view.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.ThemeAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.FragmentThemeBinding
import com.bzk.dinoteslite.utils.ReSizeView
import kotlin.math.abs

private const val TAG = "ThemeFragment"
class ThemeFragment : BaseFragment<FragmentThemeBinding>(), View.OnClickListener {
    private var themeAdapter: ThemeAdapter? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_theme
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        themeAdapter = ThemeAdapter()
        mBinding.vpgThemeChange.adapter = themeAdapter
        mBinding.dot.setViewPager2(mBinding.vpgThemeChange)
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(50))
        compositePageTransformer.addTransformer { page: View, position: Float ->
            val r = 1 - abs(position)
            page.scaleY = 0.8f + 0.15f * r
        }
        with(mBinding.vpgThemeChange) {
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(compositePageTransformer)
        }
        mBinding.vpgThemeChange.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    with(mBinding) {
                        ctlTheme.setBackgroundColor(Color.WHITE)
                        tvThemeTitle.setTextColor(Color.BLACK)
                        btnThemeChange.setTextColor(Color.BLACK)
                    }
                } else {
                    with(mBinding) {
                        ctlTheme.setBackgroundColor(Color.BLACK)
                        tvThemeTitle.setTextColor(Color.WHITE)
                        btnThemeChange.setTextColor(Color.WHITE)
                    }
                }
                val mySharedPreferences = activity?.let { MySharedPreferences(it) }
                mySharedPreferences?.pushTheme(position)
                Log.d(TAG, "onPageSelected: ${mySharedPreferences?.getTheme()}")
            }
        })
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvThemeCancel, 64)
    }

    override fun onClick() {
        mBinding.imvThemeCancel.setOnClickListener(this)
        mBinding.btnThemeChange.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_theme_cancel -> activity?.onBackPressed()
            R.id.btn_theme_change -> {
                if (Build.VERSION.SDK_INT >= 11) {
                    activity?.recreate();
                }
                //activity?.recreate()
//                activity?.finish()
//                activity?.startActivity(activity?.intent)

            }
        }
    }

}