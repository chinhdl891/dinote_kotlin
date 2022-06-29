package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemDionteBinding
import com.bzk.dinoteslite.model.Dinote

class DinoteAdapter : RecyclerView.Adapter<DinoteAdapter.DinoteViewHolder>() {
    private var listDinote: ArrayList<Dinote>? = arrayListOf<Dinote>()
    fun initData(list: MutableList<Dinote>){
        listDinote?.clear()
        listDinote?.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoteViewHolder {
        val binding: ItemDionteBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_dionte, parent, false
        )
        return DinoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DinoteViewHolder, position: Int) {
        val dinote: Dinote = listDinote!![position]
        holder.init(dinote)
    }

    override fun getItemCount(): Int {
        return listDinote?.size ?: 0
    }

    inner class DinoteViewHolder(var mBinding: ItemDionteBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun init(dinote: Dinote) {
            mBinding.setVariable(BR.dinote, dinote)
            mBinding.executePendingBindings()
        }
    }
}