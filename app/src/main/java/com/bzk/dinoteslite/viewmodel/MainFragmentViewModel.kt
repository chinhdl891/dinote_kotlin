package com.bzk.dinoteslite.viewmodel

import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.model.TagModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainFragmentViewModel"

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var dinoteDAO = DinoteDataBase.getInstance(application)?.dinoteDAO()
    var totalDinote: Int = dinoteDAO?.getTotalItemCount()!!
    var listDinote: MutableLiveData<MutableList<Dinote>> = MutableLiveData(mutableListOf())
    private var limit: Int = 50
    private var count: Int = 0
    var listHotTag: MutableLiveData<HashSet<TagModel>> = MutableLiveData(hashSetOf())
    var tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()

    fun getListHotTag(): HashSet<TagModel> {
        listHotTag.value = listHotTag.value.also {
            tagDAO?.let { listTag -> it?.addAll(listTag.getListHotTag()) }
        }
        return listHotTag.value ?: hashSetOf()
    }

    fun addHotTag(tagModel: TagModel) {
        listHotTag.value = listHotTag.value.also {
            it?.add(tagModel)
        }
    }

    var list = MutableLiveData(
        mutableListOf(
            PhotoModel(R.drawable.imv_ads_1),
            PhotoModel(R.drawable.imv_ads_2),
            PhotoModel(R.drawable.imv_ads_3),
            PhotoModel(R.drawable.imv_ads_4)
        )
    )

    fun clearData() {
        totalDinote = dinoteDAO?.getTotalItemCount()!!
        isLoading = MutableLiveData(false)
        dinoteDAO = DinoteDataBase.getInstance(getApplication())?.dinoteDAO()
        totalDinote = dinoteDAO?.getTotalItemCount()!!
        listDinote = MutableLiveData(mutableListOf())
        limit = 50
        count = 0
        listHotTag = MutableLiveData(hashSetOf())
        tagDAO = DinoteDataBase.getInstance(getApplication())?.tagDAO()
    }

    fun getListDinote(): MutableList<Dinote> {
        listDinote.value = listDinote.value?.also {
            dinoteDAO?.getAllDinote(limit, count)?.let { list -> it.addAll(list) }
        }
        return listDinote.value ?: mutableListOf()
    }

    fun deleteDinote(dinote: Dinote) {
        listDinote.value = listDinote.value.also {
            it?.remove(dinote)
            dinoteDAO?.onDelete(dinote)
        }
    }

    fun setUpLoadData() {
        isLoading.value = true
        if (count + limit > totalDinote) {
            limit = totalDinote - count
            loadData(limit, count)
        } else if (count > totalDinote) {
            isLoading.value = true
        } else {
            count += 50
            loadData(limit, count)
        }
    }

    private fun loadData(limit: Int, count: Int) {
        isLoading.value = true
        GlobalScope.launch {
            delay(2000L)
            listDinote.postValue(listDinote.value.also {
                dinoteDAO?.getAllDinote(limit, count)?.let { list ->
                    it?.addAll(list)
                    isLoading.postValue(false)
                }
            })
        }
    }

    override fun onCleared() {
        Log.e(TAG, "onCleared: ")
        super.onCleared()
    }
}



