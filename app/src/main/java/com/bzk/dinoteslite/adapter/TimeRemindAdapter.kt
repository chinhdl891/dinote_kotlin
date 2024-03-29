package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemTimeRemindBinding
import com.bzk.dinoteslite.model.TimeRemind

class TimeRemindAdapter(
    var onSetCheck: (TimeRemind) -> Unit,
    var onDelete: (TimeRemind) -> Unit,
) :
    RecyclerView.Adapter<TimeRemindAdapter.TimeRemindViewHolder>() {
    private var listTimeRemind = arrayListOf<TimeRemind>()
    fun init(list: List<TimeRemind>) {
        val timeRemindDiffUtil = TimeRemindDiffUtil(listTimeRemind,list)
        val diffResult = DiffUtil.calculateDiff(timeRemindDiffUtil)
        listTimeRemind.clear()
        listTimeRemind.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeRemindViewHolder {
        val binding: ItemTimeRemindBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_time_remind,
                parent,
                false)
        return TimeRemindViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeRemindViewHolder, position: Int) {
        var timeRemind = listTimeRemind.get(position)
        timeRemind.let { holder.bind(it) }
        holder.mBinding.swTiemOnOff.setOnCheckedChangeListener { compoundButton, b ->
            timeRemind.active = b
            timeRemind.let { onSetCheck(it) }
        }
        holder.itemView.setOnLongClickListener {
            timeRemind.let { it1 -> onDelete(it1) }
            true
        }
    }

    override fun getItemCount(): Int {
        return listTimeRemind.size
    }

    inner class TimeRemindViewHolder(var mBinding: ItemTimeRemindBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(timeRemind: TimeRemind) {
            mBinding.swTiemOnOff
            mBinding.setVariable(BR.timeRemind, timeRemind)
            mBinding.executePendingBindings()
        }
    }
}