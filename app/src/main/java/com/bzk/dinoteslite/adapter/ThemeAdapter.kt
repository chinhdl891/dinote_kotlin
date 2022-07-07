package com.bzk.dinoteslite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.FragmentThemeBinding
import com.bzk.dinoteslite.databinding.ItemPhotoAdsBinding
import com.bzk.dinoteslite.databinding.ItemThemeBinding

class ThemeAdapter : RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {
    private val listImage =
        mutableListOf<Int>(R.drawable.imv_interface_light, R.drawable.imv_interface_night)

    class ThemeViewHolder(var mBinding: ItemThemeBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(idImage: Int) {
            mBinding.imvItemPhoto.setImageResource(idImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val mBinding: ItemThemeBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_theme,
                parent,
                false)
        return ThemeViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(listImage[position])
    }

    override fun getItemCount(): Int {
        return listImage.size
    }
}