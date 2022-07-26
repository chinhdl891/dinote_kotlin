package com.bzk.dinoteslite.view.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.DinoteAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentResultSearchBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.ResultFragmentViewModel

class ResultSearchFragment : BaseFragment<FragmentResultSearchBinding>(), View.OnClickListener {
    private val viewModel by lazy {
        ViewModelProvider(this)[ResultFragmentViewModel::class.java]
    }
    private var dinoteAdapter: DinoteAdapter? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_result_search
    }

    override fun setViewStatus() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: ResultSearchFragmentArgs by navArgs()
        viewModel.contentSearch = bundle.searchContent
        viewModel.listDinoteSearch(viewModel.contentSearch)
    }

    override fun setUpdata() {
        setUpBundle()
        mBinding.tvSearchResultContent.text = viewModel.contentSearch
        viewModel.contentSearch = viewModel.contentSearch
        dinoteAdapter = DinoteAdapter(onDelete = {
            viewModel.onDelete(it)
        }, onGotoDetail = { dinote ->
            onGotoDetail(dinote)
        })
        mBinding.rcvSearchResult.adapter = dinoteAdapter
        mBinding.rcvSearchResult.layoutManager = LinearLayoutManager(activity)
        observer()
    }

    private fun setUpBundle() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(AppConstant.SEND_BUNDLE)
            ?.observe(viewLifecycleOwner) {
                val dinote = it.getSerializable(AppConstant.SEND_OBJ) as Dinote
                val status = it.getInt(AppConstant.SEND_STATUS)
                if (status == 0) {
                    viewModel.onDelete(dinote)
                }
            }
    }

    private fun onGotoDetail(dinote: Dinote) {
//        val action =
//            ResultSearchFragmentDirections.actionResultSearchFragmentToDetailFragment(dinote.id)
        val bundle = bundleOf(
            AppConstant.DEEP_LINK_ID to dinote.id
        )
        findNavController().navigate(R.id.detailFragment, bundle)
    }

    private fun observer() {
        viewModel.listDinote.observe(this) {
            dinoteAdapter?.initData(it)
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvResultCancel, 64)
    }

    override fun onClick() {
        mBinding.imvResultCancel.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_result_cancel -> findNavController().popBackStack()
        }
    }
}

