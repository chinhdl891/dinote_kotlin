package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemHotTagBinding
import com.bzk.dinoteslite.model.TagModel

class HotTagAdapter(var onSearch: (String) -> Unit) :
    RecyclerView.Adapter<HotTagAdapter.HotTagViewHolder>() {
    private val listHotTag: MutableList<TagModel> = mutableListOf<TagModel>()
    fun initData(list: MutableList<TagModel>) {
        listHotTag.also {
            it.clear()
            it.addAll(list)
            notifyDataSetChanged()
        }
    }

    class HotTagViewHolder(var mBinding: ItemHotTagBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun findData(tagModel: TagModel) {
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
        holder.findData(hotTag)
        val random = java.util.Random().nextInt(13)
        holder.mBinding.cvItemHead.setCardBackgroundColor(arrayColor()[random])
        holder.itemView.setOnClickListener {
            onSearch(hotTag.contentTag)
        }
    }

    override fun getItemCount(): Int {
        return listHotTag.size
    }

    private fun arrayColor(): IntArray {
        return intArrayOf(
            -0x25bace,
            -0x1f89c0,
            -0x185fb1,
            -0xd31a0,
            -0x28e,
            -0x6f3aac,
            -0xa76cc5,
            -0x985042,
            -0xdaaa3b,
            -0xaadd73,
            -0x7ed271,
            -0x52c46f,
            -0x25bace)
    }

}