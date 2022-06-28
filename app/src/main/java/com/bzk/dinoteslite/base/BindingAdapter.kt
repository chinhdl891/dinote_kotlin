package com.bzk.dinoteslite.base

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.net.URI

@BindingAdapter("setImageResource")
fun setImageResource(imageView: ImageView, id: Int) {
    imageView.setImageResource(id)
}

@BindingAdapter("setTextByResource")
fun setTextByResource(textView: TextView, textRes: Int) {
    textView.text = textView.context.getText(textRes)

}

@BindingAdapter("setImageByUri")
fun setImageByUri(imageView: ImageView, string: String) {
    imageView.setImageURI(Uri.parse(string))
}
