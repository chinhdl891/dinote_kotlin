package com.bzk.dinoteslite.view.dialog

import android.content.Context
import android.widget.Toast
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.databinding.DialogFakeDataBinding
import com.bzk.dinoteslite.model.Dinote

class DialogFakeData(context: Context, var onAddData: () -> Unit) :
    BaseDialog<DialogFakeDataBinding>(context) {
    override fun setUpdata() {

    }

    override fun setClick() {
        mBinding.btnFakeAdd.setOnClickListener { addData() }
        mBinding.btnFakeCancel.setOnClickListener { dismiss() }
    }

    private fun addData() {
        val count = mBinding.edtFakeNumber.text.toString().toInt()
        val dinote = Dinote(0)
        for (i in 0..count) {
            dinote.title = "fake $i"
            dinote.ListTag = emptyList()
            DinoteDataBase.getInstance(context)?.dinoteDAO()?.onInsert(dinote)
        }
        dismiss()
        onAddData()
    }

    override fun setResizeView() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_fake_data
    }
}