package com.bzk.dinoteslite.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bzk.dinoteslite.model.Dinote

class DinoteDiffUtil(var oldList: List<Dinote>, var newList: List<Dinote>) : DiffUtil.Callback() {
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