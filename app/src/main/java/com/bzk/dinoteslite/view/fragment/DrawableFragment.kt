package com.bzk.dinoteslite.view.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.provider.MediaStore
import android.view.View
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentDrawableBinding
import com.bzk.dinoteslite.utils.ReSizeView
import java.util.*

class DrawableFragment(var onSave : (String) -> Unit) : BaseFragment<FragmentDrawableBinding>(), View.OnClickListener {
    private var sizeStoke = 16
    override fun getLayoutResource(): Int {
        return R.layout.fragment_drawable
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {

    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvDrawBack, 120)
        ReSizeView.resizeView(mBinding.imvDrawSize16, 16)
        ReSizeView.resizeView(mBinding.imvDrawSize36, 36)
        ReSizeView.resizeView(mBinding.imvDrawSize64, 64)
        ReSizeView.resizeView(mBinding.imvDrawCancel, 64)
        ReSizeView.resizeView(mBinding.imvDrawSave, 64)
        ReSizeView.resizeView(mBinding.imvDrawChangeColor, 120)
        ReSizeView.resizeView(mBinding.imvDrawEraser, 120)
        ReSizeView.resizeView(mBinding.imvDrawChangeSize, 120)
    }

    override fun onClick() {
        mBinding.imvDrawBack.setOnClickListener(this)
        mBinding.imvDrawSize16.setOnClickListener(this)
        mBinding.imvDrawSize36.setOnClickListener(this)
        mBinding.imvDrawSize64.setOnClickListener(this)
        mBinding.imvDrawChangeSize.setOnClickListener(this)
        mBinding.imvDrawEraser.setOnClickListener(this)
        mBinding.imvDrawSave.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_draw_back -> mBinding.pvDrawContent.onBackDraw()
            R.id.imv_draw_size_16 -> onChangeSize(16)
            R.id.imv_draw_size_36 -> onChangeSize(36)
            R.id.imv_draw_size_64 -> onChangeSize(64)
            R.id.imv_draw_change_size -> {
                mBinding.imvDrawChangeSize.visibility = View.GONE
                mBinding.lnlDrawChangeSize.visibility = View.VISIBLE
            }
            R.id.imv_draw_eraser -> mBinding.pvDrawContent.pen(Color.WHITE, sizeStoke)
            R.id.imv_draw_cancel -> mainActivity.onBackPressed()
            R.id.imv_draw_save -> saveImage()
        }
    }

    private fun saveImage() {
        var bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        var uri : String = MediaStore.Images.Media.insertImage(mainActivity.contentResolver,
            mBinding.pvDrawContent.drawingCache,
            UUID.randomUUID().toString() + ".png",
            "Dinote")
    }

    private fun onChangeSize(size: Int) {
        sizeStoke = size
        mBinding.lnlDrawChangeSize.visibility = View.GONE
        mBinding.imvDrawChangeSize.visibility = View.VISIBLE
        mBinding.pvDrawContent.pen(brushSize = size)
    }


}