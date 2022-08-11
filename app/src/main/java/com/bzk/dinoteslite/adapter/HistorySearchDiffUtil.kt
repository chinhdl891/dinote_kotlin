package com.bzk.dinoteslite.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bzk.dinoteslite.model.HistorySearch

class HistorySearchDiffUtil(var oldList: List<HistorySearch>, var newList: List<HistorySearch>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}