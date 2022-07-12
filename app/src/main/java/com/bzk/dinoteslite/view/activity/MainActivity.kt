package com.bzk.dinoteslite.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.adapter.HotTagAdapter
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.databinding.ActivityMainBinding
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.fragment.*
import com.bzk.dinoteslite.viewmodel.MainActivityViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerAccBinding: View
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var viewModel: MainActivityViewModel
    private var hotTagAdapter: HotTagAdapter? = null
    private lateinit var imvHeadRate: ImageView
    private lateinit var imvHeadTheme: ImageView
    private lateinit var imvHeadFavorite: ImageView
    private lateinit var imvHeaderStar: ImageView
    private lateinit var rcvHeadHotTag: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(mBinding.root)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        headerAccBinding = mBinding.ngvMainAction.getHeaderView(0)
        Log.e(TAG, "onCreate: ")
        initView()
        setUpDataHead()
        setupToolBarMain()
        loadFragment(MainFragment(onAddNewTag = { newTag ->
            viewModel.listHotTag.value = viewModel.listHotTag.value.also {
                it?.add(newTag)
            }
        }), MainFragment::class.java.simpleName)
        reSizeView()
        setClick()
        observer()
        viewModel.getListHotTag()

        val timeDefault: Long = MySharedPreferences(context = this).getTimeRemindDefault()
        if (timeDefault == 0L) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val mySharedPreferences =
                MySharedPreferences(this).pushTimeRemindDefault(calendar.timeInMillis)
        }

    }

    private fun setUpDataHead() {
        rcvHeadHotTag.layoutManager = FlexboxLayoutManager(this)
        hotTagAdapter = HotTagAdapter(onSearch = {
            loadFragment(ResultSearchFragment.newInstance(it),
                ResultSearchFragment::class.simpleName.toString())
        })
        rcvHeadHotTag.adapter = hotTagAdapter
    }

    private fun initView() {
        imvHeadFavorite = headerAccBinding.findViewById(R.id.imv_head_favorite)
        imvHeadRate = headerAccBinding.findViewById(R.id.imv_head_rate)
        imvHeadTheme = headerAccBinding.findViewById(R.id.imv_head_theme)
        imvHeaderStar = headerAccBinding.findViewById(R.id.imv_header_star)
        rcvHeadHotTag = headerAccBinding.findViewById(R.id.rcv_head_tag_hot)
    }

    private fun observer() {
        viewModel.listHotTag.observe(this) {
            hotTagAdapter?.initData(it.toMutableList())
        }
    }

    fun loadFragment(fragment: Fragment, tag: String) {

        fragmentTransaction = supportFragmentManager.beginTransaction()
        if (tag != MainFragment(onAddNewTag = {}).javaClass.simpleName) {
            if (mBinding.drlMain.isDrawerOpen(GravityCompat.START)) {
                mBinding.drlMain.closeDrawer(GravityCompat.START)
            }
            mBinding.tlbMainAction.visibility = View.GONE
            if (tag == DrawableFragment::class.simpleName) {
                addFragment(fragment, tag)
            } else {
                addFragment(fragment, tag)
            }
        } else {
            addFragment(fragment, tag)
        }

    }

    private fun addFragment(fragment: Fragment, tag: String) {
        with(fragmentTransaction) {
            add(R.id.frl_main_content, fragment, fragment.javaClass.simpleName)
            addToBackStack(tag)
            commit()
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        with(fragmentTransaction) {
            replace(R.id.frl_main_content, fragment, fragment.javaClass.simpleName)
            addToBackStack(tag)
            commit()
        }
    }

    override fun onBackPressed() {
        if (mBinding.drlMain.isDrawerOpen(GravityCompat.START)) {
            mBinding.drlMain.closeDrawer(GravityCompat.START)
        }
        var getTopFragment = getTopFragment()?.tag
        if (getTopFragment != null) {
            when (getTopFragment) {
                MainFragment::class.simpleName -> onExitApp()
                DrawableFragment::class.simpleName -> super.onBackPressed()
                CreateFragment::class.simpleName,
                DetailFragment::class.simpleName,
                RemindFragment::class.simpleName,
                SearchFragment::class.simpleName,
                ThemeFragment::class.simpleName,
                -> {
                    mBinding.tlbMainAction.visibility = View.VISIBLE
                    supportFragmentManager.popBackStack()
                }
                ResultSearchFragment::class.simpleName -> {
                    val isVisitable =
                        if (supportFragmentManager.fragments.size > 2) View.GONE else View.VISIBLE
                    mBinding.tlbMainAction.visibility = isVisitable
                    supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun onExitApp() {

    }

    private fun setClick() {
        mBinding.imvMainSearch.setOnClickListener(this)
        mBinding.imvMainWatch.setOnClickListener(this)
        mBinding.imvMainNotification.setOnClickListener(this)
    }

    private fun reSizeView() {
        ReSizeView.resizeView(mBinding.imvMainNotification, 64)
        ReSizeView.resizeView(mBinding.imvMainSearch, 64)
        ReSizeView.resizeView(mBinding.imvMainWatch, 64)
        ReSizeView.resizeView(imvHeadRate, 64)
        ReSizeView.resizeView(imvHeadTheme, 64)
        ReSizeView.resizeView(imvHeadFavorite, 64)
        ReSizeView.resizeView(imvHeaderStar, 128)

    }

    private fun setupToolBarMain() {
        setSupportActionBar(mBinding.tlbMainAction)
        toggle = ActionBarDrawerToggle(this,
            mBinding.drlMain,
            mBinding.tlbMainAction,
            R.string.open,
            R.string.close)
        mBinding.drlMain.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.imv_main_watch -> gotoWatch()
            R.id.imv_main_notification -> gotoRemind()
            R.id.imv_main_search -> gotoSearch()
            R.id.lnl_head_openTheme -> goToTheme()
            R.id.imv_head_rate -> gotoWatch()
            R.id.imv_head_favorite -> gotoWatch()

        }
    }


    private fun goToTheme() {
        loadFragment(ThemeFragment(), ThemeFragment::class.simpleName.toString())
    }

    private fun gotoSearch() {
        loadFragment(SearchFragment(), SearchFragment::class.simpleName.toString())
    }

    private fun gotoRemind() {
        loadFragment(RemindFragment(), RemindFragment::class.simpleName.toString())
    }

    private fun gotoWatch() {

    }

    private fun getTopFragment(): Fragment? {
        val index = supportFragmentManager.backStackEntryCount - 1
        val backEntry = supportFragmentManager.getBackStackEntryAt(index)
        val tag = backEntry.name
        return supportFragmentManager.findFragmentByTag(tag)
    }
}
