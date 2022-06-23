package com.bzk.dinoteslite.view.fragment


import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentCreateBinding
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.DialogMotion
import com.bzk.dinoteslite.viewmodel.CreateFragmentViewModel

private const val TAG = "CreateFragment"

class CreateFragment : BaseFragment<FragmentCreateBinding>(), View.OnClickListener {
    private val viewModel: CreateFragmentViewModel by lazy {
        ViewModelProvider(this)[CreateFragmentViewModel::class.java]
    }
    private lateinit var mMotion: Motion

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        mMotion = viewModel.listMotion[1]
        mBinding.imvCreateMotion.setImageResource(mMotion.imgMotion)
        mBinding.edtCreateStatus.text = getText(mMotion.contentMotion)
        observer()
    }

    private fun observer() {
        viewModel.isFavorite.observe(this) {
            if (it == false) {
                mBinding.imvCreateTextLove.setImageResource(R.drawable.ic_text_love)
            } else {
                mBinding.imvCreateTextLove.setImageResource(R.drawable.ic_text_loved)
            }
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvCreateMotion, 48)
    }

    override fun onClick() {
        mBinding.lnlCrateStatus.setOnClickListener(this)
        mBinding.imvCreateTextLove.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.lnl_crate_status -> openDialogMotion()
            R.id.imv_create_text_love -> setFavoriteDionte()
        }
    }

    private fun setFavoriteDionte() {
        if (viewModel.getFavorite()) {
            viewModel.setFavorite(false)
        } else {
            viewModel.setFavorite(true)
        }
    }

    private fun openDialogMotion() {
        val dialogMotion = DialogMotion(mainActivity, onSelectItem = {
            mMotion = it
            mBinding.imvCreateMotion.setImageResource(it.imgMotion)
            mBinding.edtCreateStatus.text = getText(it.contentMotion)
        })
        dialogMotion.show()
    }
}