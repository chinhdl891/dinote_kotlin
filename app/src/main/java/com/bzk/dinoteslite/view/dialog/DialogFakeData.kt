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
        val count = mBinding.edtFakeNumber.text.toString().trim()
        if (count.isNotEmpty()) {
            val dinote = Dinote(0)
            val repeat = count.toInt()
            for (i in 0..repeat) {
                dinote.title = "fake $i"
                dinote.ListTag = emptyList()
                DinoteDataBase.getInstance(context)?.dinoteDAO()?.onInsert(dinote)
            }
            dismiss()
            onAddData()
        } else {
            Toast.makeText(context,
                context.getString(R.string.txt_input_number_fake_data),
                Toast.LENGTH_SHORT).show()
            mBinding.edtFakeNumber.requestFocus()
        }
    }

    override fun setResizeView() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_fake_data
    }
}