package com.bzk.dinoteslite.view.fragment


import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.mbms.FileInfo
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.AddTagAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.databinding.FragmentCreateBinding
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.DialogMotion
import com.bzk.dinoteslite.viewmodel.CreateFragmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

private const val TAG = "CreateFragment"

class CreateFragment : BaseFragment<FragmentCreateBinding>(), View.OnClickListener {
    private var deletedFile = false
    private val viewModel: CreateFragmentViewModel by lazy {
        CreateFragmentViewModel(requireActivity().application)
    }
    private lateinit var mMotion: Motion
    private val layoutManager: RecyclerView.LayoutManager by lazy {
        LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }
    private var mUri: String? = null

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
        Toast.makeText(mainActivity,
            "" + DinoteDataBase.getInstance(mainActivity)?.dinoteDAO()?.getAllDinote()?.size,
            Toast.LENGTH_SHORT).show()
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
            mBinding.rcvCreateListTag.adapter =
                AddTagAdapter(onAddTag = {
                    viewModel.addTag()
                },
                    onDeleteTag = { position ->
                        viewModel.deleteTag(position)
                    }).apply {
                    initData(viewModel.getListTag())
                    mBinding.rcvCreateListTag.layoutManager!!.scrollToPosition(it.size - 1)
                }
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvCreateMotion, 48)
    }

    override fun onClick() {
        mBinding.lnlCrateStatus.setOnClickListener(this)
        mBinding.imvCreateTextLove.setOnClickListener(this)
        mBinding.imvCreateTextEdit.setOnClickListener(this)
        mBinding.tvCreateSave.setOnClickListener(this)
        mBinding.imvCreateCancel.setOnClickListener(this)
        mBinding.tvDateSelection.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.lnl_crate_status -> openDialogMotion()
            R.id.imv_create_text_love -> setFavoriteDionte()
            R.id.imv_create_text_edit -> {
                mainActivity.loadFragment(DrawableFragment(onSave = {
                    onShowImage(it)
                }).apply {
                    val bundle = Bundle()
                    bundle.putString("old_uri", mUri)
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
        val datePickerDialog = DatePickerDialog(mainActivity,
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

    private fun cancelCreateFragment() {
        deleteFile()
        mainActivity.onBackPressed()
    }

    private fun deleteFile() {
        if (!deletedFile) {
            mUri.let {
                val uri: Uri? = Uri.parse(it)
                val file = File(uri?.path ?: "")
                if (!file.exists() && file.path != "") {
                    if (uri != null) {
                        deletedFile = true
                        mainActivity.contentResolver.delete(uri, null, null)
                    }
                }
            }
        }
    }

    private fun saveDinote() {
        viewModel.title = mBinding.edtCreateTitle.text.toString().trim()
        viewModel.content = mBinding.edtCreateContent.text.toString().trim()
        viewModel.motionId = mMotion.id
        viewModel.desImage = mBinding.edtCreateDesDrawer.text.toString().trim()
        viewModel.imageUri = mUri!!
        viewModel.insertDinote()
    }

    private fun onShowImage(uri: String) {
        mUri = uri
        mBinding.imvCreateDrawer.visibility = View.VISIBLE
        mBinding.edtCreateDesDrawer.visibility = View.VISIBLE
        mBinding.imvCreateDrawer.setImageURI(Uri.parse(uri))
    }

    private fun setFavoriteDionte() {
        if (viewModel.getFavorite()) {
            viewModel.setFavorite(false)
        } else {
            viewModel.setFavorite(true)
        }
    }

    private fun openDialogMotion() {
        val dialogMotion = DialogMotion(mainActivity, viewModel.listMotion, onSelectItem = {
            mMotion = it
            mBinding.imvCreateMotion.setImageResource(it.imgMotion)
            mBinding.edtCreateStatus.text = getText(it.contentMotion)
        })
        dialogMotion.show()
    }

}