package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemHotTagBinding
import com.bzk.dinoteslite.model.TagModel

class HotTagAdapter : RecyclerView.Adapter<HotTagAdapter.HotTagViewHolder>() {
    private val listHotTag: MutableList<TagModel> = mutableListOf<TagModel>()
    fun initData(list: MutableList<TagModel>){
        listHotTag.also {
            it.clear()
            it.addAll(list)
            notifyDataSetChanged()
        }
    }
    class HotTagViewHolder(var mBinding: ItemHotTagBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun finData(tagModel: TagModel) {
            mBinding.setVariable(BR.tag, tagModel)
            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotTagViewHolder {
        val mBinding: ItemHotTagBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_hot_tag,
                parent,
                false)
        return HotTagViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: HotTagViewHolder, position: Int) {
        val hotTag = listHotTag[position]
        hotTag.let { holder.finData(it) }
    }

    override fun getItemCount(): Int {
        return listHotTag.size
    }
}