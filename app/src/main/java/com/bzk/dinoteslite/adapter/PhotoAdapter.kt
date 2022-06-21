package com.bzk.dinoteslite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.databinding.ItemPhotoAdsBinding
import com.bzk.dinoteslite.model.PhotoModel

class PhotoAdapter : PagerAdapter() {
    private lateinit var listImage: MutableList<PhotoModel>
    private lateinit var context: Context
    private lateinit var mLayoutInflater: LayoutInflater


    fun initData(list: MutableList<PhotoModel>, context: Context) {
        this.listImage = list
        this.context = context
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listImage.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater.inflate(R.layout.item_photo_ads, container, false)
        val imageView: ImageView = itemView.findViewById(R.id.imv_item_photo)
        Glide.with(context).load(listImage[position].resID).into(imageView)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}