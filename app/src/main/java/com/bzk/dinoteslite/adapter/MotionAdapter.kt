package com.bzk.dinoteslite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemMotionBinding
import com.bzk.dinoteslite.model.Motion

class MotionAdapter(var onSelectedMotion: (Motion) -> Unit) :
    RecyclerView.Adapter<MotionAdapter.MotionViewHolder>() {
    private var listMotion: MutableList<Motion>? = mutableListOf()
    fun initData(list: MutableList<Motion>) {
        listMotion!!.clear()
        listMotion!!.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotionViewHolder {
        var layoutInflater: LayoutInflater? = null
        layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemMotionBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_motion, parent, false)
        return MotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MotionViewHolder, position: Int) {
        var motion: Motion = listMotion?.get(position) as Motion
        holder.bind(motion)
        holder.itemView.setOnClickListener {
            onSelectedMotion(motion)
        }
    }

    override fun getItemCount(): Int {
        return listMotion?.size ?: 0
    }

    class MotionViewHolder(var binding: ItemMotionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(motion: Motion) {
            binding.setVariable(BR.motion, motion)
            binding.executePendingBindings()

        }
    }

}