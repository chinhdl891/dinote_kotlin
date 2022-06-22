package com.bzk.dinoteslite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.R

class MainFragmentViewModel : ViewModel() {
    var list = MutableLiveData(mutableListOf(
        PhotoModel(R.drawable.imv_ads_1),
        PhotoModel(R.drawable.imv_ads_2),
        PhotoModel(R.drawable.imv_ads_3),
        PhotoModel(R.drawable.imv_ads_4)
    ))
}



