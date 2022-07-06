package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemSearchHistoryBinding
import com.bzk.dinoteslite.model.HistorySearch

class HistorySearchAdapter(var onSearch: (String) -> Unit) :
    RecyclerView.Adapter<HistorySearchAdapter.HistorySearchViewHolder>() {
    var listSearch: MutableList<HistorySearch> = mutableListOf()
    fun initData(mutableList: MutableList<HistorySearch>) {
        listSearch.apply {
            clear()
            addAll(mutableList)
        }
        notifyDataSetChanged()
    }

    inner class HistorySearchViewHolder(var mBinding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun findData(historySearch: HistorySearch) {
            mBinding.setVariable(BR.search, historySearch)
            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySearchViewHolder {
        val mBinding: ItemSearchHistoryBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_search_history,
                parent,
                false)
        return HistorySearchViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: HistorySearchViewHolder, position: Int) {
        val historySearch = listSearch[position]
        historySearch.let { holder.findData(it) }
        holder.itemView.setOnClickListener {
            historySearch.content?.let { content -> onSearch(content) }
        }
    }

    override fun getItemCount(): Int {
        return listSearch.size
    }
}