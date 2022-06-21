package com.bzk.dinoteslite.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected lateinit var mBinding: VB
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutResource(), container, false)
        return mBinding.root
    }

    abstract fun getLayoutResource(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        onReSize()
        setUpdata()
        setViewStatus()
    }

    protected abstract fun setViewStatus()

    protected abstract fun setUpdata()

    protected abstract fun onReSize()

    protected abstract fun onClick()


}