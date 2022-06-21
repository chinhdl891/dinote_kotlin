package com.bzk.dinoteslite.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ActivityMainBinding
import com.bzk.dinoteslite.databinding.HeaderAccBinding
import com.bzk.dinoteslite.utils.ReSizeView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerView: View
    private lateinit var headerAccBinding: HeaderAccBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBarMain()
        setupHeader()
        reSizeView()
        setClick()
    }

    private fun setClick() {
        binding.imvMainSearch.setOnClickListener(this)
        binding.imvMainWatch.setOnClickListener(this)
        binding.imvMainNotification.setOnClickListener(this)
    }

    private fun reSizeView() {
        ReSizeView.resizeView(binding.imvMainNotification, 64)
        ReSizeView.resizeView(binding.imvMainSearch, 64)
        ReSizeView.resizeView(binding.imvMainWatch, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeadRate, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeadTheme, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeadFavorite, 64)
        ReSizeView.resizeView(headerAccBinding.imvHeaderStar, 128)

    }

    private fun setupHeader() {
        headerView = binding.ngvMainAction.getHeaderView(0)
        headerAccBinding = DataBindingUtil.setContentView(this, R.layout.header_acc)

    }

    private fun setupToolBarMain() {
        setSupportActionBar(binding.tlbMainAction)
        toggle = ActionBarDrawerToggle(this,
            binding.drlMain,
            binding.tlbMainAction,
            R.string.open,
            R.string.close)
        binding.drlMain.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onClick(p0: View?) {

    }
}
