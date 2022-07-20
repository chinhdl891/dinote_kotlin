package com.bzk.dinoteslite.view.dialog

import android.content.Context
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogDeleteContentBinding

class DeleteContentDialog(context: Context, var onDelete: () -> Unit) :
    BaseDialog<DialogDeleteContentBinding>(context) {
    override fun setUpdata() {

    }

    override fun setClick() {
        mBinding.btnContinueCreateDinoteClear.setOnClickListener {
            onDelete()
            dismiss()
        }
        mBinding.btnContinueCreateDinoteCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun setResizeView() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_delete_content
    }
}