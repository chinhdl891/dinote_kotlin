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
    BaseDialog<DialogExitBinding>(context, R.style.ThemeDialogExit) {

    override fun setUpdata() {
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

}