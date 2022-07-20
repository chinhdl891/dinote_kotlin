package com.bzk.dinoteslite.view.fragment

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.HistorySearchAdapter
import com.bzk.dinoteslite.adapter.HotTagAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentSearchBinding
import com.bzk.dinoteslite.model.HistorySearch
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.SearchFragmentViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class SearchFragment : BaseFragment<FragmentSearchBinding>(), View.OnClickListener {
    private val viewModel by lazy {
        ViewModelProvider(this)[SearchFragmentViewModel::class.java]
    }
    private var hotTagAdapter: HotTagAdapter? = null
    private var searchAdapter: HistorySearchAdapter? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_search
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        mBinding.rcvSearchHistory.layoutManager = layoutManager()
        searchAdapter = HistorySearchAdapter(onSearch = {
            mBinding.editSearchContent.setText(it)
        })
        mBinding.rcvSearchHistory.adapter = searchAdapter

        hotTagAdapter = HotTagAdapter(onSearch = {
            mBinding.editSearchContent.setText(it)
        })
        mBinding.rcvSearchTagHot.adapter = hotTagAdapter
        mBinding.rcvSearchTagHot.layoutManager = layoutManager()
        viewModel.listHotTag()
        viewModel.listHistorySearch()
        observer()
    }

    private fun layoutManager(): FlexboxLayoutManager {
        val layoutManager = FlexboxLayoutManager(activity).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
            maxLine = 3
        }
        return layoutManager
    }

    private fun observer() {
        viewModel.listHistorySearch.observe(this) {
            it?.let { searchAdapter?.initData(it) }
        }
        viewModel.listHotTag.observe(this) {
            it?.let { listTag -> hotTagAdapter?.initData(listTag) }
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvSearchCancel, 64)
        ReSizeView.resizeView(mBinding.imvSearchShow, 64)
    }

    override fun onClick() {
        mBinding.imvSearchShow.setOnClickListener(this)
        mBinding.imvSearchCancel.setOnClickListener(this)
        mBinding.tvSearchDelete.setOnClickListener(this)
        mBinding.btnSearchShowMore.setOnClickListener(this)
        mBinding.btnSearchShowMore.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_search_show -> onSearch()
            R.id.imv_search_cancel -> {
                activity?.onBackPressed()
            }
            R.id.tv_search_delete -> viewModel.onDeleteSearch()
            R.id.btn_search_show_more -> showMore()
        }
    }

    private fun showMore() {
        if (isShow) {
            mBinding.rcvSearchHistory.layoutManager = layoutManager().apply { maxLine = 10 }
        } else {
            mBinding.rcvSearchHistory.layoutManager = layoutManager().apply { maxLine = 3 }
        }
        isShow = !isShow
    }

    private var isShow = true
    private fun onSearch() {
        val contentSearch = mBinding.editSearchContent.text.toString().trim()
        val search = HistorySearch(content = contentSearch)
        viewModel.onInsertHistory(search)
        val resultSearchFragment = ResultSearchFragment.newInstance(contentSearch)
//        getMainActivity()?.loadFragment(resultSearchFragment,
//            ResultSearchFragment::class.simpleName.toString())
    }
}