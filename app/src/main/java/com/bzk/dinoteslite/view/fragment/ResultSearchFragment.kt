package com.bzk.dinoteslite.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.DinoteAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentResultSearchBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.RemindFragmentViewModel
import com.bzk.dinoteslite.viewmodel.ResultFragmentViewModel
import java.text.FieldPosition

class ResultSearchFragment : BaseFragment<FragmentResultSearchBinding>(), View.OnClickListener {
    private val viewModel by lazy {
        ViewModelProvider(this)[ResultFragmentViewModel::class.java]
    }
    private var dinoteAdapter: DinoteAdapter? = null

    companion object {
        fun newInstance(search: String): ResultSearchFragment {
            val args = Bundle()
            args.putString(AppConstant.SEND_CONTENT_SEARCH, search)
            val fragment = ResultSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_result_search
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        val bundle = arguments
        val content = bundle?.getString(AppConstant.SEND_CONTENT_SEARCH) as String
        mBinding.tvSearchResultContent.text = content
        viewModel.contentSearch = content
        Toast.makeText(activity, viewModel.contentSearch, Toast.LENGTH_SHORT).show()
        dinoteAdapter = DinoteAdapter(onDelete = {
            viewModel.onDelete(it)
        }, onGotoDetail = { dinote, position ->
            onGotoDetail(dinote, position)
        })
        mBinding.rcvSearchResult.adapter = dinoteAdapter
        mBinding.rcvSearchResult.layoutManager = LinearLayoutManager(activity)
        viewModel.listDinoteSearch(content)
        observer()
    }

    private fun onGotoDetail(dinote: Dinote, position: Int) {
//        val detailFragment = DetailFragment.newInstance(dinote, position)
//        getMainActivity()?.loadFragment(detailFragment,
//            DetailFragment::class.java.simpleName.toString())
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
            R.id.imv_result_cancel -> activity?.onBackPressed()
        }
    }
}

