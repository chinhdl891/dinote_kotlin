    package com.bzk.dinoteslite.view.fragment


import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.AddTagAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.databinding.FragmentCreateBinding
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.DialogMotion
import com.bzk.dinoteslite.viewmodel.CreateFragmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CreateFragment"

class CreateFragment : BaseFragment<FragmentCreateBinding>(), View.OnClickListener {

    private val viewModel: CreateFragmentViewModel by lazy {
        CreateFragmentViewModel(requireActivity().application)
    }
    private lateinit var mMotion: Motion
    private var addTagAdapter: AddTagAdapter? = null
    private val layoutManager: RecyclerView.LayoutManager by lazy {
        LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }
    private var mUri: String? = null
    private var nameFile: String? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        mMotion = viewModel.listMotion[1]
        mBinding.imvCreateMotion.setImageResource(mMotion.imgMotion)
        mBinding.edtCreateStatus.text = getText(mMotion.contentMotion)
        mBinding.rcvCreateListTag.layoutManager = layoutManager
        mBinding.imvCreateDrawer.visibility = View.VISIBLE
        mBinding.edtCreateDesDrawer.visibility = View.VISIBLE
        addTagAdapter = AddTagAdapter(
            onAddTag = {
                viewModel.addTag()
            },
            onDeleteTag = { position ->
                viewModel.deleteTag(position)
            })
        mBinding.rcvCreateListTag.adapter = addTagAdapter
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
        viewModel.tagModelList.observe(this) {
            Log.d(TAG, "observer: " + it.size)
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
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.lnl_crate_status -> openDialogMotion()
            R.id.imv_create_text_love -> setFavoriteDionte()
            R.id.imv_create_text_edit -> {
                getMainActivity()?.loadFragment(DrawableFragment(onSave = {
                    onShowImage(it)
                }).apply {
                    val bundle = Bundle()
                    bundle.putString(AppConstant.OLD_URI, mUri)
                    arguments = bundle
                }, DrawableFragment::class.java.simpleName)
            }
            R.id.tv_create_save -> saveDinote()
            R.id.imv_create_cancel -> cancelCreateFragment()
            R.id.tv_date_selection -> setDateSelect()
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
        val datePickerDialog = getMainActivity()?.let {
            DatePickerDialog(it,
                { datePicker, i, i2, i3 ->
                    calendar.set(i, i2, i3)
                    val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy")
                    mBinding.tvDateSelection.text = simpleDateFormat.format(calendar.time)
                    viewModel.timeCreate = calendar.timeInMillis
                },
                year,
                month,
                day).show()
        }
    }

    private fun cancelCreateFragment() {
        deleteFile()
        getMainActivity()?.onBackPressed()
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
        viewModel.apply {
            title = mBinding.edtCreateTitle.text.toString().trim()
            content = mBinding.edtCreateContent.text.toString().trim()
            motionId = mMotion.id
            desImage = mBinding.edtCreateDesDrawer.text.toString().trim()
            imageUri = nameFile?.toString() ?: getString(R.string.txt_no_des)
            insertDinote()
        }
    }

    private fun onShowImage(nameFile: String) {
        this.nameFile = nameFile
        mBinding.imvCreateDrawer.visibility = View.VISIBLE
        mBinding.edtCreateDesDrawer.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mUri = getMainActivity()?.filesDir?.absolutePath + "/$nameFile"
            mBinding.imvCreateDrawer.setImageURI(Uri.parse(mUri))
        } else {
            mBinding.imvCreateDrawer.setImageURI(Uri.parse(nameFile))
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
            getMainActivity()?.let {
                DialogMotion(it, viewModel.listMotion, onSelectItem = {
                    mMotion = it
                    mBinding.imvCreateMotion.setImageResource(it.imgMotion)
                    mBinding.edtCreateStatus.text = getText(it.contentMotion)
                })
            }
        dialogMotion?.show()
    }

}