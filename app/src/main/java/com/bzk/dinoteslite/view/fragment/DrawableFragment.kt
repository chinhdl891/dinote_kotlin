package com.bzk.dinoteslite.view.fragment

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.drawToBitmap
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentDrawableBinding
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.DialogColor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

private const val TAG = "DrawableFragment"

class DrawableFragment(var onSave: (String) -> Unit) : BaseFragment<FragmentDrawableBinding>(),
    View.OnClickListener {
    private var sizeStoke = 16
    private var mColor = Color.BLACK
    override fun getLayoutResource(): Int {
        return R.layout.fragment_drawable
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        val bundle = arguments
        if (bundle != null) {
            val oldUri = bundle.getString(AppConstant.OLD_URI)
            if (oldUri != null) {
                mainActivity.contentResolver.delete(Uri.parse(oldUri), null, null)
            }
        }
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
        mBinding.imvDrawChangeColor.setOnClickListener(this)
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
            R.id.imv_draw_change_color -> changeColor()
        }
    }

    private fun changeColor() {
        val dialogColor = DialogColor(mainActivity, onSelectColor = {
            mColor = it
            mBinding.pvDrawContent.pen(color = it, sizeStoke)
        })
        dialogColor.show()
    }

    private fun saveImage() {
        var bitmap: Bitmap = mBinding.pvDrawContent.drawToBitmap(Bitmap.Config.ARGB_8888)
        onSave(saveBitMapToStores(bitmap))
        mainActivity.onBackPressed()
    }

    private fun saveBitMapToStores(bitmap: Bitmap): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveImageInQ(bitmap)
        } else {
            return saveImageUnQ(bitmap)
        }
    }

    private fun saveImageUnQ(bitmap: Bitmap): String {
        val imagesDir =
            Environment.getExternalStorageDirectory()
        val image = File(imagesDir, UUID.randomUUID().toString() + ".png")
        val fos = FileOutputStream(image)
        fos.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        return image.toURI().toString()
    }

    private fun saveImageInQ(bitmap: Bitmap): String {
        val filename = getString(R.string.txt_name_image, System.currentTimeMillis())
        val fOutputStream = mainActivity.openFileOutput(filename, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream)
        return filename
    }


    private fun onChangeSize(size: Int) {
        sizeStoke = size
        mBinding.lnlDrawChangeSize.visibility = View.GONE
        mBinding.imvDrawChangeSize.visibility = View.VISIBLE
        mBinding.pvDrawContent.pen(color = mColor, brushSize = size)
    }

}