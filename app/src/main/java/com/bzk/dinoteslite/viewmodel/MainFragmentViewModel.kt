package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.model.TagModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var dinoteDAO = DinoteDataBase.getInstance(application)?.dinoteDAO()
    var listDinote: LiveData<List<Dinote>>
    val totalItem: MutableLiveData<Int> = MutableLiveData(0)
    var listHotTag: LiveData<List<TagModel>>
    var tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()
    lateinit var dinoteLoadMore: LiveData<List<Dinote>>
    var x: Int = 0

    var list = MutableLiveData(
        mutableListOf(
            PhotoModel(R.drawable.imv_ads_1),
            PhotoModel(R.drawable.imv_ads_2),
            PhotoModel(R.drawable.imv_ads_3),
            PhotoModel(R.drawable.imv_ads_4)
        )
    )

    var limit: Int = 50
    var offSet = 0

    init {
        listDinote = dinoteDAO?.getAllDinote(limit, offSet * 50)!!
        listHotTag = tagDAO?.getListTag()!!
    }

    fun getTotalItem() {
        viewModelScope.launch(Dispatchers.IO) {
            x = dinoteDAO?.getTotalItem() ?: 0
            totalItem.postValue(x)
        }
    }

    fun loadMoreItem() {
        isLoading.postValue(true)
        offSet = if (limit + offSet >= (totalItem.value?.toInt() ?: 0)) {
            totalItem.value!!.minus(offSet)
        } else {
            offSet + 50
        }
        loadData(offSet)
    }

    fun loadData(offset: Int) {
        viewModelScope.launch {
//            listDinote.value?.plus(dinoteDAO?.getAllDinote(limit, offset))
//            dinoteLoadMore.value = dinoteDAO.getAllDinote(limit,offset)
            isLoading.postValue(false)
        }
    }

    fun deleteDinote(dinote: Dinote) {
        viewModelScope.launch(Dispatchers.IO) {
            dinoteDAO?.onDelete(dinote)
        }
    }
}






