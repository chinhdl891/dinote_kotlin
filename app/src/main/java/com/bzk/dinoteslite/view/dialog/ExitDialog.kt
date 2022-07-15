package com.bzk.dinoteslite.view.dialog

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Display
import android.view.Gravity
import android.view.WindowManager
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogExitBinding
import com.bzk.dinoteslite.utils.ReSizeView


class ExitDialog(context: Context, var onFinish: () -> Unit) :
    BaseDialog<DialogExitBinding>(context) {

    override fun setUpdata() {
        window?.let {
            val marginLeft = 80
            val attributes: WindowManager.LayoutParams = it.attributes
            attributes.x = marginLeft
            attributes.y = 0
            it.attributes = attributes
            it.setGravity(Gravity.START )
            val displaySize: Point = getDisplayDimensions(context)
            val width: Int = displaySize.x - marginLeft - marginLeft

            it.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun setClick() {
        mBinding.btnExitExit.setOnClickListener {
            onFinish()
        }
        mBinding.btnExitContinue.setOnClickListener {
            dismiss()
        }
    }

    override fun setResizeView() {
        ReSizeView.resizeView(mBinding.ivmExit, 466, 452)
    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_exit
    }

    fun getDisplayDimensions(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        var screenHeight = metrics.heightPixels

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics(metrics)
        val physicalHeight = metrics.heightPixels
        val statusBarHeight = getStatusBarHeight(context)
        val navigationBarHeight = getNavigationBarHeight(context)
        val heightDelta = physicalHeight - screenHeight
        if (heightDelta == 0 || heightDelta == navigationBarHeight) {
            screenHeight -= statusBarHeight
        }
        return Point(screenWidth, screenHeight)
    }

    fun getStatusBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources: Resources = context.resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
}