package com.bzk.dinoteslite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.R

class MainFragmentViewModel : ViewModel() {
     val list = MutableLiveData<MutableList<PhotoModel>>()


}


