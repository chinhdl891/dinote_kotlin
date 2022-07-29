package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.HistorySearch
import com.bzk.dinoteslite.model.TagModel

class SearchFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val listHistorySearch: LiveData<List<HistorySearch>>
    val listHotTag: LiveData<List<TagModel>>
    private val searchDAO = DinoteDataBase.getInstance(application)?.searchDAO()
    private val tagDAO = DinoteDataBase.getInstance(application)?.tagDAO()

    init {
        listHotTag = tagDAO?.getListHotTag()!!
        listHistorySearch = searchDAO?.getListHistory()!!
    }

    fun onInsertHistory(historySearch: HistorySearch) {
        searchDAO?.onInsert(historySearch)
    }

    fun onDeleteSearch() {
        searchDAO?.onDelete()
    }
}