package com.bzk.dinoteslite.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDAO
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote

private const val TAG = "MainFragmentViewModel"

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val context by lazy {
        getApplication<Application>().applicationContext
    }
    var list = MutableLiveData(
        mutableListOf(
            PhotoModel(R.drawable.imv_ads_1),
            PhotoModel(R.drawable.imv_ads_2),
            PhotoModel(R.drawable.imv_ads_3),
            PhotoModel(R.drawable.imv_ads_4)
        )
    )
    val dinoteDAO = DinoteDataBase.getInstance(context)?.dinoteDAO()
    var listDinote: MutableLiveData<MutableList<Dinote>> = MutableLiveData(mutableListOf())
    fun getListDinote(): MutableList<Dinote> {
        listDinote.value = listDinote.value?.also {
            it.clear()
            it.addAll(
                dinoteDAO?.getAllDinote() ?: mutableListOf()
            )
        }
        return listDinote.value ?: mutableListOf()
    }

    fun deleteDinote(dinote: Dinote){
        listDinote.value = listDinote.value.also {
            it?.remove(dinote)
            dinoteDAO?.onDelete(dinote)
        }
    }

}



