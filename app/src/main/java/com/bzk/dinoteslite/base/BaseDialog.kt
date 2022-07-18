package com.bzk.dinoteslite.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.bzk.dinoteslite.R

abstract class BaseDialog<T : ViewBinding>(
    context: Context,
    theme: Int = R.style.ThemeDialogExit,
) :
    Dialog(context, theme) {
    protected lateinit var mBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutResource(), null, false)
        setContentView(mBinding.root)
        setResizeView()
        setClick()
        setUpdata()
    }

    abstract fun setUpdata()

    abstract fun setClick()

    abstract fun setResizeView()

    abstract fun getLayoutResource(): Int
}

