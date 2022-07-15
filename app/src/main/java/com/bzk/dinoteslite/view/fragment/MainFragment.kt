package com.bzk.dinoteslite.view.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
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
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.TagModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.viewmodel.MainFragmentViewModel
import java.util.*
import kotlin.math.abs

private const val TAG = "MainFragment"
private lateinit var viewModel: MainFragmentViewModel

class MainFragment : BaseFragment<FragmentMainBinding>(),
    View.OnClickListener, DetailFragment.DetailFragmentListener {

    private var photoAdapter: PhotoAdapter? = null
    private var mTimer: Timer? = null
    private var dinoteAdapter: DinoteAdapter? = null
    private lateinit var compositePageTransformer: CompositePageTransformer

    companion object {
        fun onRemoveDinote(dinote: Dinote) {
            viewModel.listDinote.value = viewModel.listDinote.value.also {
                it?.remove(dinote)
            }
        }

        fun onUpdate(position: Int, dinote: Dinote) {
            viewModel.listDinote.value = viewModel.listDinote.value.also {
                it?.set(position, dinote)
            }
        }


    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_main
    }

    override fun setViewStatus() {

    }

    override fun setUpdata() {
        viewModel = ViewModelProvider(this)[MainFragmentViewModel::class.java]
        photoAdapter = PhotoAdapter().apply {
            submitData(viewModel.list.value!!)
        }
        observeViewModel()
        mBinding.vpgMainFragment.adapter = photoAdapter
        setViewPage()
        autoRunAds()

        val layoutManager = LinearLayoutManager(context)
        mBinding.rcvMainDinote.layoutManager = layoutManager
        dinoteAdapter = DinoteAdapter(
            onDelete = {
                viewModel.deleteDinote(it)
            },
            onGotoDetail = { dinote, position ->
                val detailFragment = DetailFragment.newInstance(dinote, position)
                getMainActivity()?.loadFragment(detailFragment,
                    DetailFragment::class.simpleName.toString())
                detailFragment.detailFragmentListener = this
            })
        mBinding.rcvMainDinote.adapter = dinoteAdapter
        viewModel.getListDinote()
        mBinding.rcvMainDinote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastVisibleItemPosition() == (viewModel.listDinote.value?.size!! - 1)) {
                    if (viewModel.totalDinote > 50) {
                        if (viewModel.isLoading.value == false) {
                            viewModel.setUpLoadData()
                        }
                    }
                }
            }
        })
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
        viewModel.listDinote.observe(viewLifecycleOwner) {
            Log.e(TAG, "observeViewModel: ")
            dinoteAdapter?.initData(it)
        }
        viewModel.isLoading.observe(this) {
            if (it) {
                mBinding.pbMainLoadMore.visibility = View.VISIBLE
            } else {
                mBinding.pbMainLoadMore.visibility = View.GONE
            }
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
        val createFragment = CreateFragment()
        createFragment.createFragmentListener = object : CreateFragment.CreateFragmentListener {
            override fun onAdd(dinote: Dinote) {
                viewModel.listDinote.value = viewModel.listDinote.value.also {
                    it?.add(0, dinote)
                }
            }

            override fun onAddTag(tagModel: TagModel) {
                addTagListener?.onAddTag(tagModel)
            }
        }
        getMainActivity()?.loadFragment(createFragment, CreateFragment::class.java.simpleName)
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

    var addTagListener: AddTagListener? = null

    interface AddTagListener {
        fun onAddTag(tagModel: TagModel)
    }

    override fun onAddTag(tagModel: TagModel) {
        addTagListener?.onAddTag(tagModel)
    }
}



