package com.bzk.dinoteslite.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.DinoteAdapter
import com.bzk.dinoteslite.adapter.PhotoAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentMainBinding
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.MainFragmentViewModel
import java.util.*
import kotlin.math.abs

private const val TAG = "MainFragment"

class MainFragment : BaseFragment<FragmentMainBinding>(), View.OnClickListener {

    private val viewModel: MainFragmentViewModel by lazy {
        ViewModelProvider(this)[MainFragmentViewModel::class.java]
    }
    private lateinit var photoAdapter: PhotoAdapter
    private var mTimer: Timer? = null
    private lateinit var dinoteAdapter: DinoteAdapter
    private lateinit var compositePageTransformer: CompositePageTransformer
    override fun getLayoutResource(): Int {
        return R.layout.fragment_main
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {

        photoAdapter = PhotoAdapter().apply {
            submitData(viewModel.list.value!!)
        }
        observeViewModel()
        mBinding.vpgMainFragment.adapter = photoAdapter
        setViewPage()
        autoRunAds()

        val layoutManager = LinearLayoutManager(context)
        mBinding.rcvMainDinote.layoutManager = layoutManager
        dinoteAdapter = DinoteAdapter(onDelete = {
            viewModel.deleteDinote(it)
        }, onGotoDetail = {
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.SEND_OBJ, it)
            var detailFragment = DetailFragment().apply {
                arguments = bundle
            }
            mainActivity.loadFragment(detailFragment, DetailFragment::class.simpleName.toString())

        })
        mBinding.rcvMainDinote.adapter = dinoteAdapter
        viewModel.getListDinote()
    }

    private fun setViewPage() {
        mBinding.vpgMainFragment.offscreenPageLimit = 3
        mBinding.vpgMainFragment.clipToPadding = false
        mBinding.vpgMainFragment.clipChildren = false
        mBinding.vpgMainFragment.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        compositePageTransformer = CompositePageTransformer().apply {
            addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.8f + 0.15f * r
            }
            addTransformer(MarginPageTransformer(50))
        }
        mBinding.vpgMainFragment.setPageTransformer(compositePageTransformer)
    }

    private fun autoRunAds() {
        mTimer = Timer()
        mTimer?.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    var positionItem: Int = mBinding.vpgMainFragment.currentItem
                    val size = viewModel.list.value!!.size - 1
                    if (positionItem < size) {
                        positionItem++
                        mBinding.vpgMainFragment.currentItem = positionItem
                    } else {
                        mBinding.vpgMainFragment.currentItem = 0
                    }
                }
            }
        }, AppConstant.TIME_DELAY, AppConstant.PERIOD)
    }

    private fun observeViewModel() {
        viewModel.listDinote.observe(this) {
            dinoteAdapter.initData(it)
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvMainCreateDinote, 80)
        ReSizeView.resizeView(mBinding.bgMainBackground, 160)

    }

    override fun onClick() {
        mBinding.bgMainBackground.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.bg_main_background -> openCreateDinote()
        }
    }

    private fun openCreateDinote() {
        mainActivity.loadFragment(CreateFragment(), CreateFragment().javaClass.simpleName)
    }

    override fun onStop() {
        super.onStop()
        mTimer?.cancel()
        mTimer = null
    }

    override fun onResume() {
        super.onResume()
        if (mTimer == null) {
            autoRunAds()
        }
    }
}


