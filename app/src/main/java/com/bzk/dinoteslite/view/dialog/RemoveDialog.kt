package com.bzk.dinoteslite.view.dialog

import android.content.Context
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogRemoveDinoteBinding

class RemoveDialog(context: Context, var onDelete: () -> Unit) :
    BaseDialog<DialogRemoveDinoteBinding>(context) {
    override fun setUpdata() {

    }

    override fun setClick() {
        mBinding.btnDialogContinuesCancel.setOnClickListener { dismiss() }
        mBinding.btnDialogDelete.setOnClickListener {
            onDelete()
            dismiss()
        }
    }

    override fun setResizeView() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_remove_dinote
    }

}