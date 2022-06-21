package com.bzk.dinoteslite.view.fragment

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.PhotoAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentMainBinding
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.MainFragmentViewModel
import java.util.*

private const val TAG = "MainFragment"

class MainFragment : BaseFragment<FragmentMainBinding>(), View.OnClickListener {
    lateinit var viewModel: MainFragmentViewModel
    lateinit var photoAdapter: PhotoAdapter
    lateinit var mTimer: Timer
    override fun getLayoutResource(): Int {
        return R.layout.fragment_main
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        mBinding.vpgMainFragment.pageMargin = 50
        photoAdapter = PhotoAdapter()
        viewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        photoAdapter.initData(listImage(), mainActivity)
        mBinding.vpgMainFragment.adapter = photoAdapter
        observeViewModel()
        autoRunAds()
    }

    private fun autoRunAds() {
        mTimer = Timer()
        mTimer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    var positionItem: Int = mBinding.vpgMainFragment.currentItem
                    val size = listImage().size - 1
                    if (positionItem < size) {
                        positionItem++
                        mBinding.vpgMainFragment.currentItem = positionItem
                    } else {
                        mBinding.vpgMainFragment.currentItem = 0
                    }
                }
            }
        }, 1500, 3000)
    }

    private fun observeViewModel() {

    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvMainCreateDinote, 80)
        ReSizeView.resizeView(mBinding.bgMainBackground, 160, 160)
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
        mainActivity.loadFragment(CreateFragment(), AppConstant.CREATE_FRAGMENT)
    }

    fun listImage(): MutableList<PhotoModel> {
        return mutableListOf(
            PhotoModel(R.drawable.imv_ads_1),
            PhotoModel(R.drawable.imv_ads_2),
            PhotoModel(R.drawable.imv_ads_3),
            PhotoModel(R.drawable.imv_ads_4)
        )
    }

    override fun onStop() {
        super.onStop()
        mTimer?.cancel() ?: null
    }
}