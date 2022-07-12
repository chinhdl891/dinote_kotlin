package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.TagModel

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val listHotTag: MutableLiveData<HashSet<TagModel>> = MutableLiveData(hashSetOf())
    val tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()

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
}