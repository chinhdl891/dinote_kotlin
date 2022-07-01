package com.bzk.dinoteslite.view.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogDeleteDinoteBinding
import com.bzk.dinoteslite.utils.ReSizeView

class DialogDelete(context: Context, var onDelete: () -> Unit) :
    BaseDialog<DialogDeleteDinoteBinding>(context),
    View.OnClickListener {

    override fun setUpdata() {
        val width = (context.resources.displayMetrics.widthPixels)
        val height = (context.resources.displayMetrics.heightPixels * 0.05).toInt()
        window?.setLayout(width, height)
        window?.setGravity(Gravity.BOTTOM)
    }

    override fun setClick() {
        mBinding.imvDialogDrop.setOnClickListener(this)
    }

    override fun setResizeView() {
        ReSizeView.resizeView(mBinding.imvDialogDrop, 64, 64)
    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_delete_dinote
    }

    override fun onClick(p0: View) {
        if (p0.id == R.id.imv_dialog_drop) {
            onDelete()
            dismiss()
        }
    }
}