package com.bzk.dinoteslite.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.Motion
import com.bzk.dinoteslite.model.TagModel

private const val TAG = "DetailFragmentViewModel"
class DetailFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var mDinote = Dinote(0)

}