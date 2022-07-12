package com.bzk.dinoteslite.view.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.AddTagAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentDetailBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.TagModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.CancelDialog
import com.bzk.dinoteslite.view.dialog.DialogMotion
import com.bzk.dinoteslite.view.dialog.SaveDinoteDialog
import com.bzk.dinoteslite.viewmodel.DetailFragmentViewModel
import java.io.File
import java.text.FieldPosition

private const val TAG = "DetailFragment"
private var mPosition: Int = 0

class DetailFragment(var onDelete: (Dinote) -> Unit, var onUpdateDinote: (Dinote, Int) -> Unit) :
    BaseFragment<FragmentDetailBinding>(),
    View.OnClickListener {
    private lateinit var nameImageNew: String
    private val viewModel by lazy {
        DetailFragmentViewModel(requireActivity().application)
    }
    private lateinit var mDinote: Dinote
    private val layoutManager by lazy {
        LinearLayoutManager(getMainActivity(), RecyclerView.HORIZONTAL, false)
    }
    private var addTagAdapter: AddTagAdapter? = null
    private var cancelDialog: CancelDialog? = null

    companion object {
        fun newInstance(dinote: Dinote, position: Int): DetailFragment {
            mPosition = position
            val args = Bundle()
            args.putSerializable(AppConstant.SEND_OBJ, dinote)
            val fragment = DetailFragment(onDelete = { dionte ->
                MainFragment.onRemoveDinote(dinote)
            }, onUpdateDinote = { dinote, position ->
                MainFragment.onUpdate(position, dinote)
            })
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_detail
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
//        onViewDisable()
        mBinding.detailViewModel = viewModel
        val bundle = arguments
        bundle?.let {
            mDinote = bundle.getSerializable(AppConstant.SEND_OBJ) as Dinote
            Log.d(TAG, "setUpdata: " + mDinote.uriImage)
            nameImageNew = mDinote.uriImage
            viewModel.mDinote = mDinote
            mBinding.setVariable(BR.dinoteDetail, mDinote)
            mBinding.executePendingBindings()
            viewModel.setFavorite(mDinote.isLike)
            if (mDinote.ListTag.isNotEmpty()) {
                viewModel.tagModelList.value = mDinote.ListTag as MutableList<TagModel>
            }
            addTagAdapter = AddTagAdapter(
                onAddTag = {
                    viewModel.addTag()
                },
                onDeleteTag = { position ->
                    viewModel.deleteTag(position)
                }
            )
            mBinding.rcvCreateTag.adapter = addTagAdapter
            mBinding.rcvCreateTag.layoutManager = layoutManager
            observer()
        }
    }

    private fun observer() {
        viewModel.tagModelList.observe(this) {
            addTagAdapter?.initData(it)
            mBinding.rcvCreateTag.layoutManager?.scrollToPosition(it.size - 1)

        }
        viewModel.setUpdate.observe(this) {
            if (it) {
                onUpdateDinote()
            }
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvDetailIsLoved, 64)
        ReSizeView.resizeView(mBinding.imvDetailIsDrop, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextLove, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextCustomText, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextEdit, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextTag, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextRemove, 64)
        ReSizeView.resizeView(mBinding.imvCreateMotion, 48)
    }

    override fun onClick() {

        mBinding.apply {
            imvDetailIsLoved.setOnClickListener(this@DetailFragment::onClick)
            imvDetailIsDrop.setOnClickListener(this@DetailFragment::onClick)
            imvDetailCancel.setOnClickListener(this@DetailFragment::onClick)
            tvDetailUpdate.setOnClickListener(this@DetailFragment::onClick)
            imvCreateTextLove.setOnClickListener(this@DetailFragment::onClick)
            imvCreateTextCustomText.setOnClickListener(this@DetailFragment::onClick)
            imvCreateTextEdit.setOnClickListener(this@DetailFragment::onClick)
            imvCreateTextTag.setOnClickListener(this@DetailFragment::onClick)
            imvCreateTextRemove.setOnClickListener(this@DetailFragment::onClick)
            lnlCrateStatus.setOnClickListener(this@DetailFragment::onClick)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_detail_is_loved -> {
                onSetFavorite()
            }
            R.id.imv_detail_is_drop -> {
                onDropDinote()
            }
            R.id.imv_create_text_edit -> {
                onGotoDrawFragment()
            }
            R.id.imv_create_text_love -> {
                onSetFavorite()
            }
            R.id.imv_create_text_tag -> {}
            R.id.imv_create_text_remove -> {
                mBinding.edtCreateContent.setText("")
            }
            R.id.imv_detail_cancel -> {
                onCancel()
            }
            R.id.tv_detail_update -> {
                onUpdateDinote()
            }
            R.id.lnl_crate_status -> {
                onSelectMotion()
            }
        }
    }

    private fun onCancel() {
        cancelDialog = activity?.let {
            CancelDialog(it, onCancel = {
                val nameOld = viewModel.mDinote.uriImage
                if (nameOld != nameImageNew) {
                    val filePath: String =
                        getMainActivity()?.filesDir?.absolutePath + "/$nameImageNew"
                    val file = File(filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                    activity?.onBackPressed()
                }
            })
        }
        cancelDialog?.show()
    }

    private fun onGotoDrawFragment() {
        getMainActivity()?.loadFragment(DrawableFragment(onSave = { nameImage ->
            onShowDraw(nameImage)
        }), DrawableFragment::class.simpleName.toString())
    }

    private fun onShowDraw(nameImage: String) {
        nameImageNew = nameImage
        var uri: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getMainActivity()?.filesDir?.absolutePath + "/$nameImage"
        } else {
            nameImage
        }
        mBinding.imvCreateDrawer.visibility = View.VISIBLE
        mBinding.edtCreateDesDrawer.visibility = View.VISIBLE
        mBinding.imvCreateDrawer.setImageURI(Uri.parse(uri))
        viewModel.mDinote.uriImage = uri
    }

    private fun onSelectMotion() {
        val dialogMotion =
            getMainActivity()?.let {
                DialogMotion(it, viewModel.listMotion, onSelectItem = { motion ->
                    viewModel.mDinote.motion = motion.id
                    mBinding.imvCreateMotion.setImageResource(motion.imgMotion)
                    mBinding.edtCreateStatus.setText(motion.contentMotion)
                }).show()
            }
    }

    private fun onDropDinote() {
        viewModel.dropDinote(mDinote)
        onDelete(mDinote)
        activity?.onBackPressed()
    }

    private fun onSetFavorite() {
        val isLike = viewModel.getFavorite()
        viewModel.setFavorite(!isLike)
        if (isLike) {
            mBinding.imvCreateTextLove.setImageResource(R.drawable.ic_text_loved)
            mBinding.imvDetailIsLoved.setImageResource(R.drawable.ic_text_loved)
        } else {
            mBinding.imvCreateTextLove.setImageResource(R.drawable.ic_text_love)
            mBinding.imvDetailIsLoved.setImageResource(R.drawable.ic_text_love)
        }
    }

    private fun onUpdateDinote() {
        viewModel.mDinote.apply {
            title = mBinding.edtCreateTitle.text.toString().trim()
            content = mBinding.edtCreateContent.text.toString().trim()
            desImage = mBinding.edtCreateDesDrawer.text.toString().trim()
            onUpdate()
        }
    }

    private fun onUpdate() {
        viewModel.updateDinote()
        onUpdateDinote(viewModel.mDinote, mPosition)
    }

}