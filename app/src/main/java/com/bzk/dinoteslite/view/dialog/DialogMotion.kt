package com.bzk.dinoteslite.view.dialog

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.MotionAdapter
import com.bzk.dinoteslite.base.BaseDialog
import com.bzk.dinoteslite.databinding.DialogMotionBinding
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.CreateFragmentViewModel

class DialogMotion(context: Context, var onSelectItem : (Motion) -> Unit) : BaseDialog<DialogMotionBinding>(context) {
    private var motionAdapter: MotionAdapter? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    override fun setClick() {

    }

    override fun setResizeView() {
        ReSizeView.resizeView(mBinding.lnlDialogMotionBackground , 936, 1120)
    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_motion
    }

    override fun setUpdata() {
        layoutManager = GridLayoutManager(context, 3)
        motionAdapter = MotionAdapter(onSelectedMotion = {
            onSelectItem(it)
            dismiss()
        }).apply {
            initData(CreateFragmentViewModel().listMotion)
        }
        mBinding.rcvDialogMotion.layoutManager = layoutManager
        mBinding.rcvDialogMotion.adapter = motionAdapter
    }
}