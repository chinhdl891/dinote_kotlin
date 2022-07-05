package com.bzk.dinoteslite.view.fragment

import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentSearchBinding
import com.bzk.dinoteslite.utils.ReSizeView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private var hotTagAdapter
    override fun getLayoutResource(): Int {
        return R.layout.fragment_search
    }

    override fun setViewStatus() {
        ReSizeView.resizeView(mBinding.imvSearchCancel, 64)
        ReSizeView.resizeView(mBinding.imvSearchShow, 64)
    }

    override fun setUpdata() {
        val layoutManager = FlexboxLayoutManager(activity)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.FLEX_START
        mBinding.rcvSearchTagHot.layoutManager = layoutManager

    }

    override fun onReSize() {

    }

    override fun onClick() {

    }

}