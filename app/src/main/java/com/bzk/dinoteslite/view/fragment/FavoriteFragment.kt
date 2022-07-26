package com.bzk.dinoteslite.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.DinoteAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentFavoriteBinding
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.FavoriteFragmentViewModel
import com.bzk.dinoteslite.viewmodel.MainFragmentViewModel

private lateinit var viewModel: FavoriteFragmentViewModel

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(), View.OnClickListener {

    private val layoutManager = LinearLayoutManager(activity)
    private var dinoteAdapter: DinoteAdapter? = null
    override fun getLayoutResource(): Int {
        return R.layout.fragment_favorite
    }

    override fun setViewStatus() {
    }

    override fun setUpdata() {
        viewModel = ViewModelProvider(this)[FavoriteFragmentViewModel::class.java]
        observer()
        viewModel.getListDinote()
        setUpDinoteAdapter()
        setupLoadMore()
    }

    private fun setupLoadMore() {
        mBinding.rcvFavoriteDinote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastVisibleItemPosition() == (viewModel.listFavorite.value?.size!! - 1)) {
                    if (viewModel.totalItem!! > 50) {
                        if (!viewModel.isLoading.value!!) {
                            viewModel.setUpLoadData()
                        }
                    }
                }
            }
        })
    }

    private fun setUpDinoteAdapter() {
        if (viewModel.totalItem == 0) {
            mBinding.rcvFavoriteDinote.visibility = View.GONE
        }
        dinoteAdapter = DinoteAdapter(onDelete = {

        }, onGotoDetail = { dinote ->
//            val action = MainFragmentDirections.actionMainFragmentToDetailFragment(dinote.id)
            val bundle  = bundleOf(
                AppConstant.DEEP_LINK_ID to dinote.id
            )
            findNavController().navigate(R.id.detailFragment, bundle)
        })
        mBinding.rcvFavoriteDinote.adapter = dinoteAdapter
        mBinding.rcvFavoriteDinote.layoutManager = layoutManager
    }

    private fun observer() {
        viewModel.listFavorite.observe(this) {
            dinoteAdapter?.initData(it)
        }
        viewModel.isLoading.observe(this) {
            if (it) {
                mBinding.pbFavoriteLoad.visibility = View.VISIBLE
            } else {
                mBinding.pbFavoriteLoad.visibility = View.GONE
            }
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvFavoriteEmpty, 264)
    }

    override fun onClick() {
        mBinding.imvFavoriteCancel.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_favorite_cancel -> activity?.onBackPressed()
        }
    }
}