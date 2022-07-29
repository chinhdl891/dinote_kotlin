package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.Dinote

class ResultFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var listDinote = MutableLiveData(mutableListOf<Dinote>())
    val dinoteDAO = DinoteDataBase.getInstance(application)?.dinoteDAO()
    var contentSearch = ""
    fun listDinoteSearch(search: String): List<Dinote> {
        dinoteDAO?.let {
            listDinote.value?.addAll(
                it.getListBySearch(search)
            )
        }
        return listDinote.value ?: mutableListOf()
    }

    fun onDelete(dinote: Dinote) {
        listDinote.value = listDinote.value.also {
            it?.remove(dinote)
            dinoteDAO?.onDelete(dinote)
        }
    }
}