package com.bzk.dinoteslite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.ViewDataBindingKtx
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemPhotoAdsBinding
import com.bzk.dinoteslite.model.PhotoModel
import com.bzk.dinoteslite.utils.ReSizeView

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private var listImage : MutableList<PhotoModel> = mutableListOf<PhotoModel>()

    fun submitData(list: List<PhotoModel>) {
        listImage.clear()
        listImage.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_ads, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(listImage[position])
    }

    override fun getItemCount(): Int {
       return listImage.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photoModel: PhotoModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.imv_item_photo)
            imageView.setImageResource(photoModel.resID)
        }
    }

}