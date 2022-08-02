package com.bzk.dinoteslite.view.fragment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.AddTagAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.databinding.FragmentDetailBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.TagModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.*
import com.bzk.dinoteslite.viewmodel.DetailFragmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "DetailFragment"

class DetailFragment : BaseFragment<FragmentDetailBinding>(),
    View.OnClickListener {

    private lateinit var nameImageNew: String
    private val viewModel by lazy {
        DetailFragmentViewModel(requireActivity().application)
    }
    private lateinit var mDinote: Dinote
    private var addTagAdapter: AddTagAdapter? = null
    private var cancelDialog: CancelDialog? = null


    override fun getLayoutResource(): Int {
        return R.layout.fragment_detail
    }

    override fun setViewStatus() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        val id = bundle?.getInt(AppConstant.DEEP_LINK_ID) as Int
        viewModel.id = id
        viewModel.setUpdate
        initData(id)
        viewModel.getListTag()
        viewModel.getFavorite()
        observer()
    }

    private fun initData(id: Int?) {
        mDinote = context?.let {
            id?.let { id ->
                DinoteDataBase.getInstance(it)?.dinoteDAO()?.getDinoteById(id)
            }
        }!!
        nameImageNew = mDinote.uriImage
        viewModel.mDinote = mDinote
        if (mDinote.ListTag.isNotEmpty()) {
            viewModel.tagModelList.value = viewModel.tagModelList.value.also {
                it?.addAll(0, mDinote.ListTag)
            }
        }
    }

    override fun setUpdata() {
        checkImageIsExits(mDinote.uriImage)
        getDataFromDrawFragment()
        mBinding.detailViewModel = viewModel
        mBinding.setVariable(BR.dinoteDetail, mDinote)
        mBinding.executePendingBindings()
        setUpTagAdapter()
    }

    private fun setUpTagAdapter() {
        addTagAdapter = AddTagAdapter(
            onAddTag = {
                viewModel.addTag()
            },
            onDeleteTag = { position ->
                viewModel.deleteTag(position)
            }
        )
        mBinding.rcvCreateTag.adapter = addTagAdapter
    }

    private fun getDataFromDrawFragment() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(AppConstant.SEND_URI)
            ?.observe(viewLifecycleOwner) { uri ->
                onShowDraw(uri)
            }
    }

    private fun checkImageIsExits(uriImage: String) {
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context?.filesDir?.absolutePath + "/$uriImage")
        } else {
            File(Uri.parse(uriImage).toString())
            with(uriImage) {
                replace("file:", "").trim()
            }
            File(Uri.parse(uriImage).path!!)
        }
        if (file.exists()) {
            mBinding.lnlDetailDraw.visibility = View.VISIBLE
        }
    }

    private fun observer() {
        viewModel.tagModelList.observe(this) {
            mBinding.rcvCreateTag.layoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            addTagAdapter?.initData(it)
            mBinding.rcvCreateTag.layoutManager?.scrollToPosition(it.size - 1)
        }
        viewModel.setUpdate.observe(this) {
            if (it) {
                onUpdateDinote()
                viewModel.setUpdate.value = !it
            } else {
                mBinding.lnlCrateOption.visibility = ViewGroup.GONE
            }
        }
        viewModel.isUpdate.observe(this) {
            if (it) {
                mBinding.lnlCrateOption.visibility = ViewGroup.VISIBLE
                viewModel.tagModelList.value = viewModel.tagModelList.value.also { tagList ->
                    tagList?.add(TagModel(0, ""))
                    if (mDinote.content.isEmpty()) {
                        mBinding.edtCreateContent.hint = getString(R.string.txt_input_content)
                    }
                    if (mDinote.desImage.isEmpty()) {
                        mBinding.edtCreateDesDrawer.hint = getString(R.string.txt_input_des_image)
                    }
                    if (mDinote.title.isEmpty()) {
                        mBinding.edtCreateTitle.hint = getString(R.string.txt_input_title)
                    }
                }
            } else {
                if (mDinote.content.isEmpty()) {
                    mBinding.edtCreateContent.hint = getString(R.string.txt_no_content)
                }
                if (mDinote.desImage.isEmpty()) {
                    mBinding.edtCreateDesDrawer.hint = getString(R.string.txt_no_desciption)
                }
                if (mDinote.title.isEmpty()) {
                    mBinding.edtCreateTitle.hint = getString(R.string.txt_no_title)
                }
            }
        }
        viewModel.isFavorite.observe(this) {
            var src = if (it) R.drawable.ic_text_loved else R.drawable.ic_text_love
            setImageLove(src)
            viewModel.setFavoriteDinote(it)
        }
    }

    private fun setImageLove(src: Int) {
        mBinding.imvCreateTextLove.setImageResource(src)
        mBinding.imvDetailIsLoved.setImageResource(src)
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvDetailIsLoved, 64)
        ReSizeView.resizeView(mBinding.imvDetailIsDrop, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextLove, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextCustomText, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextEdit, 64)
        ReSizeView.resizeView(mBinding.imvCreateTextTagEdit, 64)
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
            imvCreateTextTagEdit.setOnClickListener(this@DetailFragment::onClick)
            imvCreateTextRemove.setOnClickListener(this@DetailFragment::onClick)
            lnlCrateStatus.setOnClickListener(this@DetailFragment::onClick)
            tvDateSelection.setOnClickListener(this@DetailFragment::onClick)
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
            R.id.imv_create_text_remove -> {
                remoteContent()
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
            R.id.imv_create_text_tag_edit -> {
                mBinding.edtCreateTitle.requestFocus()
                viewModel.addTag()
                mBinding.edtCreateTitle.clearFocus()
            }
            R.id.tv_date_selection -> {
                onSelectDate()
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

    private fun onSelectDate() {
        var date = Date()
        date.time = mDinote.dateCreate
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
                    mBinding.tvDateSelection.text = simpleDateFormat.format(calendar.timeInMillis)
                    viewModel.mDinote.dateCreate = calendar.timeInMillis
                },
                year,
                month,
                day).show()
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
                }
                cancelDialog?.dismiss()
                if (viewModel.isUpdate.value == true) {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.detailFragment,
                        bundleOf(AppConstant.DEEP_LINK_ID to viewModel.id,
                            AppConstant.SEND_STATUS to 0))
                } else {
                    findNavController().popBackStack()
                }
            })
        }
        cancelDialog?.show()
    }

    private fun onGotoDrawFragment() {
        findNavController().navigate(R.id.drawableFragment)
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
        viewModel.mDinote.uriImage = nameImageNew
    }

    private fun onSelectMotion() {
        getMainActivity()?.let {
            DialogMotion(it, viewModel.listMotion, onSelectItem = { motion ->
                viewModel.mDinote.motion = motion.id
                mBinding.imvCreateMotion.setImageResource(motion.imgMotion)
                mBinding.edtCreateStatus.setText(motion.contentMotion)
            }).show()
        }
    }

    private fun onDropDinote() {
        context?.let {
            RemoveDialog(it, onDelete = {
                viewModel.dropDinote(mDinote)
//                val bundle = bundleOf(
//                    AppConstant.SEND_OBJ to viewModel.mDinote,
//                    AppConstant.SEND_STATUS to 0
//                )
//                findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.SEND_BUNDLE,
//                    bundle)
                findNavController().popBackStack()
            }).show()
        }

    }

    private fun onSetFavorite() {
        viewModel.isFavorite.value = !viewModel.isFavorite.value!!
    }

    private fun onUpdateDinote() {
        mBinding.edtCreateContent.requestFocus()
        context?.let {
            UpdateDialog(it, onUpdate = {
                viewModel.blockView.set(ViewGroup.FOCUS_BLOCK_DESCENDANTS)
                mBinding.edtCreateContent.isFocused
                viewModel.mDinote.apply {
                    title = mBinding.edtCreateTitle.text.toString().trim()
                    content = mBinding.edtCreateContent.text.toString().trim()
                    desImage = mBinding.edtCreateDesDrawer.text.toString().trim()
                    onUpdate()
                    viewModel.tagModelList.value?.forEach { tag ->
                        detailFragmentListener?.onAddTag(tag)
                    }
                }
            }, onCancel = {
                viewModel.blockView.set(ViewGroup.FOCUS_BEFORE_DESCENDANTS)
            }).show()
        }
    }

    private fun onUpdate() {
        viewModel.updateDinote()
        findNavController().popBackStack()
        findNavController().navigate(R.id.detailFragment,
            bundleOf(AppConstant.DEEP_LINK_ID to viewModel.id,
                AppConstant.SEND_STATUS to 0))
    }

    var detailFragmentListener: DetailFragmentListener? = null

    interface DetailFragmentListener {
        fun onAddTag(tagModel: TagModel)
    }
}