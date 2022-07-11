package com.bzk.dinoteslite.view.dialog

import android.content.Context
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogSaveBinding
import com.bzk.dinoteslite.utils.ReSizeView

class SaveDinoteDialog(context: Context, var onSave: () -> Unit) :
    BaseDialog<DialogSaveBinding>(context) {
    override fun setUpdata() {

    }

    override fun setClick() {
        mBinding.btnSaveDialog.setOnClickListener {
            onSave()
            dismiss()
        }
    }

    override fun setResizeView() {
        ReSizeView.resizeView(mBinding.imvCompleteDialog, 180, 180)
    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_save
    }
}