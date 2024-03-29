package com.bzk.dinoteslite.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bzk.dinoteslite.database.DinoteDataBase
import com.bzk.dinoteslite.database.sharedPreferences.MySharedPreferences
import com.bzk.dinoteslite.model.TimeRemind
import java.text.SimpleDateFormat
import java.util.*

class RemindFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val timeRemindDAO = DinoteDataBase.getInstance(application)?.timeRemindDAO()
    var listTimeRemind: LiveData<List<TimeRemind>> = timeRemindDAO?.getListTimeRemindLiveData()!!
    //    fun listTimeRemind(): MutableList<TimeRemind> {
//        listTimeRemind.value = listTimeRemind.value?.also {
//            it.clear()
//            timeRemindDAO?.let { it1 -> it.addAll(it1.getListTimeRemind()) }
//        }
//        return listTimeRemind.value ?: mutableListOf()
//    }

    fun deleteTimeRemind(timeRemind: TimeRemind) {
        timeRemindDAO?.onDeleteTime(timeRemind)
    }

    fun insertTimeRemind(timeRemind: TimeRemind) {
        timeRemindDAO?.onInsertTime(timeRemind)
    }

    fun update(timeRemind: TimeRemind) {
        timeRemindDAO?.onUpdateTime(timeRemind)
    }

//    fun getTimeForPush(): Long {
//        check = true
//        val time: Long? = timeRemindDAO?.getListTimeRemind()?.sortedBy { it.time }?.filter {
//            it.time > System.currentTimeMillis() && it.active
//        }?.get(0)?.time
//        return time ?: MySharedPreferences(getApplication()).getTimeRemindDefault()
//    }
}