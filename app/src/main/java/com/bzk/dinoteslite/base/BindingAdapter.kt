package com.bzk.dinoteslite.base

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bzk.dinoteslite.R
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("setImageResource")
fun setImageResource(imageView: ImageView, id: Int) {
    imageView.setImageResource(id)
}

@BindingAdapter("setTextByResource")
fun setTextByResource(textView: TextView, textRes: Int) {
    textView.text = textView.context.getText(textRes)

}

//@BindingAdapter("setImageByURI")
//fun setImageByUri(imageView: ImageView, string: String) {
//    if (string.isEmpty()) {
//        imageView.setImageURI(null);
//    } else {
//        imageView.setImageURI(Uri.parse(string));
//    }
//}

@BindingAdapter("setMonthTime")
fun setDateMonth(textView: TextView, time: Long) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val month = calendar.get(Calendar.MONTH) + 1
    textView.text = String.format("Th %d", month)
    textView.textSize = 11F
}

@BindingAdapter("setYearTime")
fun setDateYear(textView: TextView, time: Long) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val year = calendar.get(Calendar.YEAR)
    textView.text = "$year"
    textView.textSize = 11F
}

@BindingAdapter("setDayTime")
fun setDateDay(textView: TextView, time: Long) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val day = calendar.get(Calendar.DATE)
    textView.text = String.format(" %d", day)
    textView.textSize = 15F
}

@BindingAdapter("setDate")
fun setDate(textView: TextView, time: Long) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy")
    textView.text = simpleDateFormat.format(time)
}

@BindingAdapter("setImageIsLikeByBoolean")
fun setImageIsLikeByBoolean(imageView: ImageView, boolean: Boolean) {
    if (boolean) {
        imageView.setImageResource(R.drawable.ic_text_loved)
    } else {
        imageView.setImageResource(R.drawable.ic_text_love)
    }
}