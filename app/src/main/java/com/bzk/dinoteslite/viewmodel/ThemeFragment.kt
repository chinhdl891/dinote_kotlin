package com.bzk.dinoteslite.viewmodel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentThemeBinding
import com.bzk.dinoteslite.utils.ReSizeView

class ThemeFragment : BaseFragment<FragmentThemeBinding>(), View.OnClickListener {
    override fun getLayoutResource(): Int {
        return R.layout.fragment_theme
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {

    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvThemeCancel, 64)
    }

    override fun onClick() {
        mBinding.imvThemeCancel.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.imv_theme_cancel -> activity?.theme
        }
    }

}