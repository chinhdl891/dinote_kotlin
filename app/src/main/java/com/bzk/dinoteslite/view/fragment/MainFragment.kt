package com.bzk.dinoteslite.view.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
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
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.FragmentMainBinding
import com.bzk.dinoteslite.model.TagModel
import com.bzk.dinoteslite.reciver.TimeRemindReceiver
import com.bzk.dinoteslite.utils.AppConstant
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.activity.MainActivity
import com.bzk.dinoteslite.view.dialog.DialogFakeData
import com.bzk.dinoteslite.viewmodel.MainFragmentViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.*
import kotlin.math.abs


private lateinit var viewModel: MainFragmentViewModel
private lateinit var drawerLayout: DrawerLayout

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
    private lateinit var lnlHeadTheme: LinearLayout
    private lateinit var lnlHeadFavorite: LinearLayout
    private lateinit var lnlHeadRate: LinearLayout
    private var timeRemindDefault: Long = 0L

    companion object {
        fun closeDrawer() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainFragmentViewModel::class.java]
        viewModel.loadData(viewModel.limit)
        viewModel.getTotalItem()
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
        onClickHead()
        setUpPhotoAdapter()
        setUpDinoteAdapter()
        setUpDataHead()
        setupToolBarMain()
        observeViewModel()
        drawerLayout = mBinding.drlMain
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
        lnlHeadFavorite = headerView.findViewById(R.id.lnl_head_open_favorite)
        lnlHeadRate = headerView.findViewById(R.id.lnl_head_open_rate)
        lnlHeadTheme = headerView.findViewById(R.id.lnl_head_openTheme)
    }

    private fun setupLoadMore() {
        mBinding.rcvMainDinote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager?.findLastVisibleItemPosition() == (viewModel.limit - 1)) {
                    Toast.makeText(activity,
                        "${layoutManager?.findLastVisibleItemPosition()}",
                        Toast.LENGTH_SHORT).show()
                    if (viewModel.totalItem.value!! >= 50) {
                        if (viewModel.isLoading.value == false) {
                            viewModel.loadMoreItem()
                        }
                    }
                }
            }
        })
    }

    private fun setUpDinoteAdapter() {
        layoutManager = LinearLayoutManager(activity)
        mBinding.rcvMainDinote.layoutManager = layoutManager
        dinoteAdapter = DinoteAdapter(
            onDelete = {
                viewModel.deleteDinote(it)
            },
            onGotoDetail = { dinote ->
                val bundle = bundleOf(
                    AppConstant.DEEP_LINK_ID to dinote.id
                )
                findNavController().navigate(R.id.detailFragment, bundle)
            })
        mBinding.rcvMainDinote.adapter = dinoteAdapter
    }

    private fun setUpPhotoAdapter() {
        photoAdapter = PhotoAdapter().apply {
            submitData(viewModel.list.value!!)
        }
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
            viewModel.getTotalItem()
            dinoteAdapter?.initData(it.toMutableList())
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                mBinding.pbMainLoadMore.visibility = View.VISIBLE
            } else {
                mBinding.pbMainLoadMore.visibility = View.GONE
            }
        }

        viewModel.listHotTag.observe(this) {
            hotTagAdapter?.initData(it.toMutableList())
        }
    }

    override fun onReSize() {
        ReSizeView.resizeView(mBinding.imvMainCreateDinote, 80)
        ReSizeView.resizeView(mBinding.bgMainBackground, 160)
        ReSizeView.resizeView(mBinding.imvMainPlusData, 80)
        ReSizeView.resizeView(mBinding.cvMainBackgroundPlus, 160)
    }

    private fun onClickHead() {
        lnlHeadTheme.setOnClickListener(this)
        lnlHeadRate.setOnClickListener(this)
        lnlHeadFavorite.setOnClickListener(this)
    }

    override fun onClick() {
        mBinding.bgMainBackground.setOnClickListener(this)
        mBinding.cvMainBackgroundPlus.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.bg_main_background -> openCreateDinote()
            R.id.cv_main_background_plus -> openFakeData()
            R.id.lnl_head_openTheme -> openTheme()
            R.id.lnl_head_open_rate -> openRate()
            R.id.lnl_head_open_favorite -> openFavorite()
        }
    }

    private fun openFavorite() {
        findNavController().navigate(R.id.favoriteFragment)
    }

    private fun openRate() {

    }

    private fun openTheme() {
        findNavController().navigate(R.id.themeFragment)
    }

    private fun openFakeData() {
        activity?.let {
            DialogFakeData(it, onAddData = {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                it.startActivity(intent)
                it.finishAffinity()
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
        if (mBinding.drlMain.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        }
    }

    private var addTagListener: AddTagListener? = null

    interface AddTagListener {
        fun onAddTag(tagModel: TagModel)
    }

    override fun onAddTag(tagModel: TagModel) {
        addTagListener?.onAddTag(tagModel)
    }

    private fun setUpDataHead() {
        rcvHeadHotTag.layoutManager = FlexboxLayoutManager(activity)
        hotTagAdapter = HotTagAdapter(onSearch = {
            val action = MainFragmentDirections.actionMainFragmentToResultSearchFragment(it)
            findNavController().navigate(action)
        })
        rcvHeadHotTag.adapter = hotTagAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}



