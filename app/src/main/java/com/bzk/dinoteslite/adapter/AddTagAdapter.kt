package com.bzk.dinoteslite.adapter

import android.nfc.Tag
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemAddTagBinding

class AddTagAdapter : RecyclerView.Adapter<AddTagAdapter.AddTagViewHolder>() {
    private var listTag = mutableListOf<Tag>()
    fun initData(list: MutableList<Tag>) {
        listTag.clear()
        listTag = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddTagViewHolder {
        val binding: ItemAddTagBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_add_tag,
                parent,
                false)
        return AddTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddTagViewHolder, position: Int) {
        val tag: Tag = listTag[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int {
        return listTag.size ?: 0
    }

    class AddTagViewHolder(var mBinding: ItemAddTagBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(tag: Tag) {
            mBinding.setVariable(BR.tag, tag)
            mBinding.executePendingBindings()
        }
    }
}