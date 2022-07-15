package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoriteFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val listFavorite: MutableLiveData<MutableList<Dinote>> = MutableLiveData(mutableListOf())
    private var limit: Int = 50
    private var count: Int = 0
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var dinoteDAO = DinoteDataBase.getInstance(application)?.dinoteDAO()
    var totalItem: Int? = dinoteDAO?.getAllDinoteFavorite(limit, count)?.size

    fun getListDinote(): MutableList<Dinote> {
        listFavorite.value = listFavorite.value.also {
            dinoteDAO?.let { listDinote ->
                it?.addAll(listDinote.getAllDinoteFavorite(limit,
                    count))
            }
        }
        return listFavorite.value ?: mutableListOf()
    }

    fun setUpLoadData() {
        isLoading.value = true
        if (count + limit > totalItem!!) {
            limit = totalItem!! - count
            loadData(limit, count)
        } else if (count > totalItem!!) {
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
            listFavorite.postValue(listFavorite.value.also {
                dinoteDAO?.getAllDinote(limit, count)?.let { list ->
                    it?.addAll(list)
                    isLoading.postValue(false)
                }
            })
        }
    }

    fun deleteDinote(dinote: Dinote) {
        listFavorite.value = listFavorite.value.also {
            it?.remove(dinote)
            dinoteDAO?.onDelete(dinote)
        }
    }
}