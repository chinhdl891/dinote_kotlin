package com.bzk.dinoteslite.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentDetailBinding
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.viewmodel.DetailFragmentViewModel

class DetailFragment : BaseFragment<FragmentDetailBinding>() {
   private val viewModel by lazy {
       DetailFragmentViewModel(requireActivity().application)
   }
    override fun getLayoutResource(): Int {
       return R.layout.fragment_detail
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        val bundle = arguments
        if (bundle != null) {
            bundle.getSerializable(AppConstant.SEND_OBJ)
        }
    }

    override fun onReSize() {

    }

    override fun onClick() {

    }

}