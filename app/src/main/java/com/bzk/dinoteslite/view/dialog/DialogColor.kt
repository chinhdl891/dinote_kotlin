package com.bzk.dinoteslite.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogColorBinding
import com.bzk.dinoteslite.utils.ReSizeView

class DialogColor(context: Context, var onSelectColor: (Int) -> Unit) :
    BaseDialog<DialogColorBinding>(context), View.OnClickListener {
    private var mBitmap: Bitmap? = null
    private var colorSelect: Int = 0x000000

    override fun setUpdata() {
        mBinding.imvDialogColor.apply {
            isDrawingCacheEnabled = true
            buildDrawingCache(true)
            setOnTouchListener { view, motionEvent ->
                if (motionEvent.y > 0 && motionEvent.x > 0 && motionEvent.x < 300 && motionEvent.y < 300) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE) {
                        mBitmap = this.drawingCache
                        val pixels =
                            mBitmap!!.getPixel(motionEvent.x.toInt(), motionEvent.y.toInt())
                        val r = Color.red(pixels)
                        val g = Color.green(pixels)
                        val b = Color.blue(pixels)
                        colorSelect = Color.rgb(r, g, b)
                        mBinding.viewDialogColorPilot.setBackgroundColor(colorSelect)
                    }
                }
                return@setOnTouchListener true
            }
        }
    }

    override fun setClick() {
        mBinding.tvDialogApplyColor.setOnClickListener(this)
    }

    override fun setResizeView() {
        ReSizeView.resizeView(mBinding.imvDialogColor, 300, 300)
        ReSizeView.resizeView(mBinding.viewDialogColorPilot, 300, 100)
    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_color
    }

    override fun onClick(p0: View) {
        if (p0.id == R.id.tv_dialog_apply_color) {
            onSelectColor(colorSelect)
            dismiss()
        }
    }

}