package com.bzk.dinoteslite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemDionteBinding
import com.bzk.dinoteslite.model.Dinote
import com.bzk.dinoteslite.view.dialog.DialogDelete

class DinoteAdapter(var onDelete: (Dinote) -> Unit, var onGotoDetail: (Dinote) -> Unit) :
    RecyclerView.Adapter<DinoteAdapter.DinoteViewHolder>() {
    private var listDinote: ArrayList<Dinote> = arrayListOf<Dinote>()
    private lateinit var mContext: Context
    fun initData(list: MutableList<Dinote>) {
        listDinote.clear()
        listDinote.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoteViewHolder {
        mContext = parent.context
        val binding: ItemDionteBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_dionte, parent, false
        )
        return DinoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DinoteViewHolder, position: Int) {
        val dinote: Dinote = listDinote[position]
        holder.init(dinote)
        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            DialogDelete(mContext, onDelete = {
                Toast.makeText(mContext, "$position", Toast.LENGTH_SHORT).show()
                onDelete(dinote)
            }).show()
            true
        })
        holder.itemView.setOnClickListener(View.OnClickListener {
            onGotoDetail(dinote)
        })
    }

    override fun getItemCount(): Int {
        return listDinote.size
    }

    inner class DinoteViewHolder(var mBinding: ItemDionteBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun init(dinote: Dinote) {
            mBinding.setVariable(BR.dinote, dinote)
            mBinding.executePendingBindings()
        }
    }
}