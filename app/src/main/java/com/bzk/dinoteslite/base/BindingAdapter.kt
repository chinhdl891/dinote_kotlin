package com.bzk.dinoteslite.base

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("setImageResource")
fun setImageResource(imageView: ImageView, id: Int) {
    imageView.setImageResource(id)
}

@BindingAdapter("setTextByResource")
fun setTextByResource(textView: TextView, textRes: Int) {
    textView.text = textView.context.getText(textRes)

}