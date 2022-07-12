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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainFragmentViewModel"

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val dinoteDAO = DinoteDataBase.getInstance(application)?.dinoteDAO()
    var totalDinote: Int = dinoteDAO?.getTotalItemCount()!!
    var listDinote: MutableLiveData<MutableList<Dinote>> = MutableLiveData(mutableListOf())

    private var limit: Int = 50
    private var count: Int = 0

    var list = MutableLiveData(
        mutableListOf(
            PhotoModel(R.drawable.imv_ads_1),
            PhotoModel(R.drawable.imv_ads_2),
            PhotoModel(R.drawable.imv_ads_3),
            PhotoModel(R.drawable.imv_ads_4)
        )
    )

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

    fun clearList() {
        listDinote.value = listDinote.value.also { it?.clear() }
    }

    override fun onCleared() {
        Log.e(TAG, "onCleared: ")
        super.onCleared()
    }
}



