package com.bzk.dinoteslite.base

import android.app.Application
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.bzk.dinoteslite.R
import com.bzk.dinoteslite.viewmodel.DetailFragmentViewModel
import java.io.File
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

@BindingAdapter("setImageByURI")
fun setImageByUri(imageView: ImageView, uri: String?) {
    if (uri?.isEmpty() == true) {
        imageView.setImageURI(null);
    } else {
        //file:/storage/emulated/0/671d80a2-c98a-4491-906e-f2a969afaa6b.png
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val file = File(imageView.context.filesDir.absolutePath + "/$uri")
            setImage(imageView, file)
        } else {
            val file = File(uri)
            setImage(imageView, file)
        }

    }
}

fun setImage(imageView: ImageView, file: File) {
    if (file.exists()) {
        imageView.setImageURI(Uri.parse(file.toURI().toString()))
    } else {
        imageView.setImageURI(null);
    }
}

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
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    textView.text = simpleDateFormat.format(calendar.timeInMillis)
}

@BindingAdapter("setImageIsLikeByBoolean")
fun setImageIsLikeByBoolean(imageView: ImageView, isLike: Boolean) {
    if (isLike) {
        imageView.setImageResource(R.drawable.ic_text_loved)
    } else {
        imageView.setImageResource(R.drawable.ic_text_love)
    }
}

@BindingAdapter("setImageMotion")
fun setImageMotion(imageView: ImageView, position: Int) {
    val motion =
        DetailFragmentViewModel(imageView.context.applicationContext as Application).listMotion[position]
    imageView.setImageResource(motion.imgMotion)
}

@BindingAdapter("setTextViewMotion")
fun setTextByRes(textView: TextView, position: Int) {
    val motion =
        DetailFragmentViewModel(textView.context.applicationContext as Application).listMotion[position]
    textView.setText(motion.contentMotion)
}

@BindingAdapter("setTextTimeRemind")
fun setTextTimeRemind(textView: TextView, time: Long) {
    val calendar = Calendar.getInstance().apply {
        this.timeInMillis = time
    }
    val simpleDateFormat = SimpleDateFormat("HH:mm a")
    textView.text = simpleDateFormat.format(calendar.timeInMillis)
}

@BindingAdapter("setStatusTimeRemind")
fun setStatusRemind(switchCompat: SwitchCompat, status: Boolean) {
    switchCompat.isEnabled = status
}

