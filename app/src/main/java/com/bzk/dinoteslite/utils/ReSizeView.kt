package com.bzk.dinoteslite.utils

import android.app.ActionBar
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup

class ReSizeView {
    companion object {

        private fun getDisplayInfo(): DisplayMetrics {
            return Resources.getSystem().displayMetrics
        }

        fun resizeView(view: View, width: Int, height: Int = 0) {
            val height2: Int = if (height == 0) width else height
            val pW = getDisplayInfo().widthPixels * width / AppConstant.DISPLAY
            val pH = pW * height2 / width
            val params: ViewGroup.LayoutParams = view.layoutParams
            params.let {
                params.height = pH
                params.width = pW
            }
        }
    }
}