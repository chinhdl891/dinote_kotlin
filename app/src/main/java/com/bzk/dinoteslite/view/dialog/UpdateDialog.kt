package com.bzk.dinoteslite.view.dialog

import android.content.Context
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogUpdateDinoteBinding

class UpdateDialog(context: Context, var onUpdate: () -> Unit, var onCancel: () -> Unit) :
    BaseDialog<DialogUpdateDinoteBinding>(context) {
    override fun setUpdata() {

    }

    override fun setClick() {
        mBinding.btnChangeDinoteContinue.setOnClickListener {
            onCancel()
            dismiss()
        }
        mBinding.btnChangeDinoteSave.setOnClickListener {
            onUpdate()
            dismiss()
        }
    }

    override fun setResizeView() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_update_dinote
    }
}