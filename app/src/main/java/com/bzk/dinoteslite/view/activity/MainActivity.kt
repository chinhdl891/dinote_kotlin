package com.bzk.dinoteslite.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ActivityMainBinding
import com.bzk.dinoteslite.databinding.HeaderAccBinding
import com.bzk.dinoteslite.utils.ReSizeView
import com.bzk.dinoteslite.view.fragment.CreateFragment
import com.bzk.dinoteslite.view.fragment.DrawableFragment
import com.bzk.dinoteslite.view.fragment.MainFragment

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerAccBinding: HeaderAccBinding
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(mBinding.root)
        setupHeader()
        setupToolBarMain()
        loadFragment(MainFragment(), MainFragment::class.java.simpleName)
        reSizeView()
        setClick()
    }

    fun loadFragment(fragment: Fragment, tag: String) {

        fragmentTransaction = supportFragmentManager.beginTransaction()
        if (tag != MainFragment().javaClass.simpleName) {
            if (mBinding.drlMain.isDrawerOpen(GravityCompat.START)) {
                mBinding.drlMain.closeDrawer(GravityCompat.START)
            }
            mBinding.tlbMainAction.visibility = View.GONE
            if (tag == DrawableFragment::class.simpleName) {
                addFragment(fragment, tag)
            } else {
                replaceFragment(fragment, tag)
            }
        } else {
            replaceFragment(fragment, tag)
        }

    }

    private fun addFragment(fragment: Fragment, tag: String) {
        fragmentTransaction.add(R.id.frl_main_content, fragment, fragment.javaClass.simpleName)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        fragmentTransaction.replace(R.id.frl_main_content, fragment, fragment.javaClass.simpleName)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (mBinding.drlMain.isDrawerOpen(GravityCompat.START)) {
            mBinding.drlMain.closeDrawer(GravityCompat.START)
        }
        var getTopFragment = getTopFragment()?.tag
        if (getTopFragment != null) {
            when(getTopFragment){
                MainFragment::class.simpleName -> onExitApp()
                DrawableFragment::class.simpleName -> super.onBackPressed()
                CreateFragment::class.simpleName ->{
                    mBinding.tlbMainAction.visibility = View.VISIBLE
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
        headerAccBinding.imvHeadTheme.setOnClickListener(this)
        headerAccBinding.imvHeadFavorite.setOnClickListener(this)
        headerAccBinding.imvHeadRate.setOnClickListener(this)
    }

    private fun reSizeView() {
        ReSizeView.resizeView(mBinding.imvMainNotification, 64)
        ReSizeView.resizeView(mBinding.imvMainSearch, 64)
        ReSizeView.resizeView(mBinding.imvMainWatch, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeadRate, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeadTheme, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeadFavorite, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeaderStar, 128)

    }

    private fun setupHeader() {
        headerAccBinding = HeaderAccBinding.inflate(LayoutInflater.from(this))
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
            R.id.imv_main_notification -> gotoWatch()
            R.id.imv_main_search -> gotoWatch()
            R.id.imv_head_theme -> gotoWatch()
            R.id.imv_head_rate -> gotoWatch()
            R.id.imv_head_favorite -> gotoWatch()
        }
    }

    private fun gotoWatch() {

    }

    fun getTopFragment(): Fragment? {
        val index = supportFragmentManager.backStackEntryCount - 1
        val backEntry = supportFragmentManager.getBackStackEntryAt(index)
        val tag = backEntry.name
        return supportFragmentManager.findFragmentByTag(tag)
    }
}
