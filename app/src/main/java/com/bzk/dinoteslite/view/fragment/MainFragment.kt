package com.bzk.dinoteslite.view.fragment

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.DinoteAdapter
import com.bzk.dinoteslite.adapter.HotTagAdapter
import com.bzk.dinoteslite.adapter.PhotoAdapter
import com.bzk.dinoteslite.base.BaseFragment
import com.bzk.dinoteslite.databinding.FragmentMainBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.TagModel
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.dialog.DialogFakeData
import com.bzk.dinoteslite.viewmodel.MainFragmentViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.*
import kotlin.math.abs

private const val TAG = "MainFragment"
private lateinit var viewModel: MainFragmentViewModel

class MainFragment : BaseFragment<FragmentMainBinding>(),
    View.OnClickListener, DetailFragment.DetailFragmentListener {

    private var photoAdapter: PhotoAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var mTimer: Timer? = null
    private var dinoteAdapter: DinoteAdapter? = null
    private lateinit var compositePageTransformer: CompositePageTransformer
    private lateinit var headerView: View
    private lateinit var imvHeadRate: ImageView
    private lateinit var imvHeadTheme: ImageView
    private lateinit var imvHeadFavorite: ImageView
    private lateinit var imvHeaderStar: ImageView
    private lateinit var imvHeaderHotTag: ImageView
    private var hotTagAdapter: HotTagAdapter? = null
    private lateinit var rcvHeadHotTag: RecyclerView
    private lateinit var toggle: ActionBarDrawerToggle

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
        headerView = mBinding.ngvMainAction.getHeaderView(0)
        initHeadView()
        reSizeView()
        viewModel = ViewModelProvider(this)[MainFragmentViewModel::class.java]
        layoutManager = LinearLayoutManager(activity)
        mBinding.rcvMainDinote.layoutManager = layoutManager
        setUpPhotoAdapter()
        setUpDinoteAdpter()
        viewModel.getListDinote()
        layoutManager = LinearLayoutManager(context)
        setupLoadMore()
        setupToolBarMain()
    }

    private fun reSizeView() {
        ReSizeView.resizeView(imvHeadFavorite, 64)
        ReSizeView.resizeView(imvHeadTheme, 64)
        ReSizeView.resizeView(imvHeadRate, 64)
        ReSizeView.resizeView(imvHeaderStar, 128)
        ReSizeView.resizeView(imvHeaderHotTag, 64)
    }

    private fun setupToolBarMain() {
        mBinding.mtbMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_menu_search -> {
                    findNavController().navigate(R.id.searchFragment)
                    true
                }
                R.id.item_menu_notification -> {
                    findNavController().navigate(R.id.remindFragment)
                    true
                }
                R.id.item_menu_watch -> {
                    true
                }
                else -> false
            }
        }
        toggle = ActionBarDrawerToggle(activity,
            mBinding.drlMain,
            mBinding.mtbMain,
            R.string.open,
            R.string.close)
        mBinding.drlMain.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initHeadView() {
        imvHeadFavorite = headerView.findViewById(R.id.imv_head_favorite)
        imvHeadRate = headerView.findViewById(R.id.imv_head_rate)
        imvHeadTheme = headerView.findViewById(R.id.imv_head_theme)
        imvHeaderStar = headerView.findViewById(R.id.imv_header_star)
        rcvHeadHotTag = headerView.findViewById(R.id.rcv_head_tag_hot)
        imvHeaderHotTag = headerView.findViewById(R.id.imv_head_tag)
    }

    private fun setupLoadMore() {
        mBinding.rcvMainDinote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager?.findLastVisibleItemPosition() == (viewModel.listDinote.value?.size!! - 1)) {
                    if (viewModel.totalDinote > 50) {
                        if (viewModel.isLoading.value == false) {
                            viewModel.setUpLoadData()
                        }
                    }
                }
            }
        })
    }

    private fun setUpDinoteAdpter() {
        dinoteAdapter = DinoteAdapter(
            onDelete = {
                viewModel.deleteDinote(it)
            },
            onGotoDetail = { dinote, position ->
                val action = MainFragmentDirections.actionMainFragmentToDetailFragment(dinote)
                findNavController().navigate(action)
            })
        mBinding.rcvMainDinote.adapter = dinoteAdapter
    }

    private fun setUpPhotoAdapter() {
        photoAdapter = PhotoAdapter().apply {
            submitData(viewModel.list.value!!)
        }
        observeViewModel()
        mBinding.vpgMainFragment.adapter = photoAdapter
        setViewPage()
        autoRunAds()

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
        ReSizeView.resizeView(mBinding.imvMainPlusData, 80)
        ReSizeView.resizeView(mBinding.cvMainBackgroundPlus, 160)
    }

    override fun onClick() {
        mBinding.bgMainBackground.setOnClickListener(this)
        mBinding.cvMainBackgroundPlus.setOnClickListener(this)

    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.bg_main_background -> openCreateDinote()
            R.id.cv_main_background_plus -> openFakeData()
        }
    }

    private fun openFakeData() {
        activity?.let {
            DialogFakeData(it, onAddData = {
                requireActivity().recreate()
            }).show()
        }
    }

    private fun openCreateDinote() {
        findNavController().navigate(R.id.createFragment)
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

    fun setEnableDraw() {
        mBinding.drlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun setDisableDraw() {
        mBinding.drlMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private fun setUpDataHead() {
        rcvHeadHotTag.layoutManager = FlexboxLayoutManager(activity)
        hotTagAdapter = HotTagAdapter(onSearch = {

        })
        rcvHeadHotTag.adapter = hotTagAdapter
    }

    override fun onDestroyView() {
        viewModel.clearData()
        super.onDestroyView()
    }

}



