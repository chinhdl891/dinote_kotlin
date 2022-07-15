package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.BR
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemSlideViewPagerBinding
import com.bzk.dinoteslite.model.SlideModel


class TutorialAdapter : RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {

    private var listTutorial: List<SlideModel> = mutableListOf()
    fun initData(listModel: List<SlideModel>) {
        listTutorial = listModel
    }

    inner class TutorialViewHolder(var mBinding: ItemSlideViewPagerBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun binding(slideModel: SlideModel) {
            mBinding.setVariable(BR.slide, slideModel)
            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val view: ItemSlideViewPagerBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_slide_view_pager,
                parent,
                false)
        return TutorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        val slideModel = listTutorial[position]
        holder.binding(slideModel)

    }

    override fun getItemCount(): Int {
        return listTutorial.size
    }
}