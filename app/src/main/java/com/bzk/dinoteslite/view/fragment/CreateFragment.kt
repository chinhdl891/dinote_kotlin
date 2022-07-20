package com.bzk.dinoteslite.view.fragment


import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.AddTagAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentCreateBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.model.TagModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.CancelDialog
import com.bzk.dinoteslite.view.dialog.DeleteContentDialog
import com.bzk.dinoteslite.view.dialog.DialogMotion
import com.bzk.dinoteslite.view.dialog.SaveDinoteDialog
import com.bzk.dinoteslite.viewmodel.CreateFragmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CreateFragment"

class CreateFragment :
    BaseFragment<FragmentCreateBinding>(), View.OnClickListener {
    private val viewModel: CreateFragmentViewModel by lazy {
        CreateFragmentViewModel(requireActivity().application)
    }
    private lateinit var mMotion: Motion
    private var addTagAdapter: AddTagAdapter? = null
    private var mUri: String? = null
    private var nameFile: String? = null
    private var cancelDialog: CancelDialog? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        setUpBundle()
        mMotion = viewModel.listMotion[1]
        mBinding.imvCreateMotion.setImageResource(mMotion.imgMotion)
        mBinding.edtCreateStatus.text = getText(mMotion.contentMotion)
        mBinding.imvCreateDrawer.visibility = View.VISIBLE
        mBinding.edtCreateDesDrawer.visibility = View.VISIBLE
        observer()
    }

    private fun setUpBundle() {
        val bundle = arguments
        if (bundle != null) {
            val stringUri: String = bundle.getString(AppConstant.SEND_URI, "")
            onShowImage(stringUri)
        }
    }

    private fun observer() {
        viewModel.isFavorite.observe(this) {
            if (it == false) {
                mBinding.imvCreateTextLove.setImageResource(R.drawable.ic_text_love)
            } else {
                mBinding.imvCreateTextLove.setImageResource(R.drawable.ic_text_loved)
            }
        }
        viewModel.tagModelList.observe(this) {
            mBinding.rcvCreateListTag.layoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            addTagAdapter = AddTagAdapter(
                onAddTag = {
                    viewModel.addTag()
                },
                onDeleteTag = { position ->
                    viewModel.deleteTag(position)
                })
            mBinding.rcvCreateListTag.adapter = addTagAdapter
            addTagAdapter?.apply {
                initData(viewModel.getListTag())
            }
            mBinding.rcvCreateListTag.layoutManager!!.scrollToPosition(it.size - 1)
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvCreateMotion, 48)
    }

    override fun onClick() {
        mBinding.apply {
            lnlCrateStatus.setOnClickListener(this@CreateFragment::onClick)
            imvCreateTextLove.setOnClickListener(this@CreateFragment::onClick)
            imvCreateTextEdit.setOnClickListener(this@CreateFragment::onClick)
            tvCreateSave.setOnClickListener(this@CreateFragment::onClick)
            imvCreateCancel.setOnClickListener(this@CreateFragment::onClick)
            tvDateSelection.setOnClickListener(this@CreateFragment::onClick)
            imvCreateTextAddTag.setOnClickListener(this@CreateFragment::onClick)
            imvCreateTextRemove.setOnClickListener(this@CreateFragment::onClick)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.lnl_crate_status -> openDialogMotion()
            R.id.imv_create_text_love -> setFavoriteDionte()
            R.id.imv_create_text_edit -> {
                val bundle = bundleOf(
                    AppConstant.SEND_ADDRESS to CreateFragment::class.java.simpleName
                )
                findNavController().navigate(R.id.drawableFragment, bundle)
            }
            R.id.imv_create_text_remove -> remoteContent()
            R.id.tv_create_save -> saveDinote()
            R.id.imv_create_cancel -> cancelCreateFragment()
            R.id.tv_date_selection -> setDateSelect()
            R.id.imv_create_text_add_tag -> {
                mBinding.edtCreateTitle.requestFocus()
                viewModel.addTag()
                mBinding.edtCreateTitle.clearFocus()
            }
        }
    }

    private fun remoteContent() {
        activity?.let {
            DeleteContentDialog(it, onDelete = {
                mBinding.edtCreateContent.setText("")
            }).show()
        }
    }

    private fun setDateSelect() {
        var date = Date()
        date.time = System.currentTimeMillis()
        var calendar = Calendar.getInstance()
        calendar.timeInMillis = date.time
        val day = calendar.get(Calendar.DATE)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        getMainActivity()?.let {
            DatePickerDialog(it,
                { datePicker, i, i2, i3 ->
                    calendar.set(i, i2, i3)
                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    mBinding.tvDateSelection.text = simpleDateFormat.format(calendar.time)
                    viewModel.timeCreate = calendar.timeInMillis
                },
                year,
                month,
                day).show()
        }
    }

    private fun cancelCreateFragment() {
        cancelDialog = context?.let {
            CancelDialog(it, onCancel = {
                deleteFile()
                getMainActivity()?.onBackPressed()
            })
        }
        cancelDialog?.show()
    }

    private fun deleteFile() {
        if (mUri != null) {
            val file = File(mUri)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun saveDinote() {
        mBinding.edtCreateContent.requestFocus()
        viewModel.apply {
            title = mBinding.edtCreateTitle.text.toString().trim()
            content = mBinding.edtCreateContent.text.toString().trim()
            motionId = mMotion.id
            desImage = mBinding.edtCreateDesDrawer.text.toString().trim()
            imageUri = nameFile?.toString() ?: getString(R.string.txt_no_des)
            insertDinote()
        }
        viewModel.dinote?.let { createFragmentListener?.onAdd(it) }
        activity?.let {
            SaveDinoteDialog(it, onSave = {
                viewModel.checkTagIsEmpty()
                viewModel.lisTagIsEmpty.forEach { tag ->
                    createFragmentListener?.onAddTag(tag)
                }
                findNavController().navigate(R.id.mainFragment)
            }).show()
        }
    }

    private fun onShowImage(nameFile: String) {
        this.nameFile = nameFile
        mBinding.imvCreateDrawer.visibility = View.VISIBLE
        mBinding.edtCreateDesDrawer.visibility = View.VISIBLE
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mUri = getMainActivity()?.filesDir?.absolutePath + "/$nameFile"
                mBinding.imvCreateDrawer.setImageURI(Uri.parse(mUri))
            } else {
                mBinding.imvCreateDrawer.setImageURI(Uri.parse(nameFile))
            }
            mBinding.lnlCreateImvDes.visibility = View.VISIBLE
        } catch (e: Exception) {
            mBinding.imvCreateDrawer.visibility = View.GONE
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
        val dialogMotion =
            getMainActivity()?.let { it ->
                DialogMotion(it, viewModel.listMotion, onSelectItem = { motion ->
                    mMotion = motion
                    mBinding.imvCreateMotion.setImageResource(motion.imgMotion)
                    mBinding.edtCreateStatus.text = getText(motion.contentMotion)
                })
            }
        dialogMotion?.show()
    }

    var createFragmentListener: CreateFragmentListener? = null

    interface CreateFragmentListener {
        fun onAddTag(tagModel: TagModel)
        fun onAdd(dinote: Dinote)
    }
}