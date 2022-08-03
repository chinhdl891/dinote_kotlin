package com.bzk.dinoteslite.view.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentDrawableBinding
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.DialogColor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DrawableFragment : BaseFragment<FragmentDrawableBinding>(),
    View.OnClickListener {
    private var sizeStoke = 16
    private var mColor = Color.BLACK
    private lateinit var stringUri: String
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
                activity?.contentResolver?.delete(Uri.parse(oldUri), null, null)
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
        mBinding.imvDrawCancel.setOnClickListener(this)
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
            R.id.imv_draw_cancel -> findNavController().popBackStack()
            R.id.imv_draw_save -> saveImage()
            R.id.imv_draw_change_color -> changeColor()
        }
    }

    private fun changeColor() {
        val dialogColor = getMainActivity()?.let {
            DialogColor(it, onSelectColor = { color ->
                mColor = color
                mBinding.pvDrawContent.pen(color = color, sizeStoke)
            })
        }
        dialogColor?.show()
    }

    private fun saveImage() {
        val bitmap: Bitmap = mBinding.pvDrawContent.drawToBitmap(Bitmap.Config.ARGB_8888)
        saveBitMapToStores(bitmap)
        //pass data
        findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.SEND_URI,
            stringUri)
        findNavController().popBackStack()
    }

    private fun saveBitMapToStores(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runBlocking {
                val job: Deferred<String> = lifecycleScope.async { saveImageInQ(bitmap) }
                stringUri = job.await()
            }
        } else {
            runBlocking {
                val job: Deferred<String> = lifecycleScope.async { saveImageUnQ(bitmap) }
                stringUri = job.await()
            }
        }
    }

    private fun saveImageUnQ(bitmap: Bitmap): String {
        val imagesDir =
            Environment.getExternalStorageDirectory()
        val image = File(imagesDir, UUID.randomUUID().toString() + ".png")
        val fos = FileOutputStream(image)
        fos.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        return image.toURI().path
    }

    private fun saveImageInQ(bitmap: Bitmap): String {
        val filename = getString(R.string.txt_name_image, System.currentTimeMillis())
        val fOutputStream = activity?.openFileOutput(filename, Context.MODE_PRIVATE)
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