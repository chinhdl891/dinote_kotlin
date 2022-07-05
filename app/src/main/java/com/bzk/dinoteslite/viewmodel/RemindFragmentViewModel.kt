package com.bzk.dinoteslite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.model.TimeRemind

class RemindFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val context by lazy {
        getApplication<Application>().applicationContext
    }
    private val timeRemindDAO = DinoteDataBase.getInstance(application)?.timeRemindDAO()
    val listTimeRemind: MutableLiveData<MutableList<TimeRemind>> = MutableLiveData(mutableListOf())
    fun listTimeRemind(): MutableList<TimeRemind> {
        listTimeRemind.value = listTimeRemind.value?.also {
            it.clear()
            timeRemindDAO?.let { it1 -> it.addAll(it1.getListTimeRemind()) }
        }
        return listTimeRemind.value ?: mutableListOf()
    }

    fun deleteTimeRemind(timeRemind: TimeRemind) {
        listTimeRemind.value = listTimeRemind.value?.also {
            it.remove(timeRemind)
            timeRemindDAO?.onDeleteTime(timeRemind)
        }
    }

    fun insertTimeRemind(timeRemind: TimeRemind) {
        listTimeRemind.value = listTimeRemind.value?.also {
            it.add(timeRemind)
            timeRemindDAO?.onInsertTime(timeRemind)
        }
    }

    fun update(position: Int, timeRemind: TimeRemind) {
        listTimeRemind.value = listTimeRemind.value?.also {
            it[position] = timeRemind
            timeRemindDAO?.onUpdateTime(timeRemind)
        }
    }
}