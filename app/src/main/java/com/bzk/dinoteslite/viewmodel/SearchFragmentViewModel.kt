package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.HistorySearch
import com.bzk.dinoteslite.model.TagModel

class SearchFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val listHistorySearch: MutableLiveData<MutableList<HistorySearch>?> = MutableLiveData(
        mutableListOf())
    val listHotTag: MutableLiveData<MutableList<TagModel>?> = MutableLiveData(mutableListOf())
    val searchDAO = DinoteDataBase.getInstance(application)?.searchDAO()
    val tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()
    fun listHistorySearch(): MutableList<HistorySearch> {
        listHistorySearch.value = listHistorySearch.value.also {
            searchDAO?.getListHistory()?.let { listSearch -> it?.addAll(listSearch) }
        }
        return listHistorySearch.value ?: mutableListOf<HistorySearch>()
    }

    fun onInsertHistory(historySearch: HistorySearch) {
        listHistorySearch.value = listHistorySearch.value.also {
            it?.add(historySearch)
            searchDAO?.onInsert(historySearch)
        }
    }

    fun onDeleteSearch() {
        listHistorySearch.value = listHistorySearch.value.also {
            it?.clear()
            searchDAO?.onDelete()
        }
    }
}