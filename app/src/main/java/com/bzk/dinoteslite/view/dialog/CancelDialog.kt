package com.bzk.dinoteslite.view.dialog

import android.content.Context
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogCancelDinoteBinding

class CancelDialog(context: Context, var onCancel: () -> Unit) :
    BaseDialog<DialogCancelDinoteBinding>(context) {
    override fun setUpdata() {

    }

    override fun setClick() {
        mBinding.btnDialogDinoteExit.setOnClickListener {
            onCancel()
            dismiss()
        }
        mBinding.btnDialogDinoteContinue.setOnClickListener { dismiss() }
    }

    override fun setResizeView() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_cancel_dinote
    }
}